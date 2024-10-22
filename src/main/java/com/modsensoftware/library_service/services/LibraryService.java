package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.clients.BookServiceClient;
import com.modsensoftware.library_service.dtos.BookDTO;
import com.modsensoftware.library_service.exceptions.BookLoanNotFoundException;
import com.modsensoftware.library_service.exceptions.BookServiceBookNotFoundException;
import com.modsensoftware.library_service.exceptions.UserNotFoundException;
import com.modsensoftware.library_service.models.*;
import com.modsensoftware.library_service.repositories.BookLoanRepository;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.requests.AddBooksToLibraryRequest;
import com.modsensoftware.library_service.requests.BorrowBookByIdOnDaysRequest;
import com.modsensoftware.library_service.requests.ReturnBookRequest;
import com.modsensoftware.library_service.responses.BookLoanResponse;
import com.modsensoftware.library_service.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final BookServiceClient bookServiceClient;
    private final LibraryInventoryService libraryInventory;
    private final LoanBookQuantityService loanBookQuantityService;
    private final BookLoanRepository bookLoanRepository;
    private final UserRepository userRepository;

    public BookLoanResponse borrowBookOnDays(
        BorrowBookByIdOnDaysRequest borrowBookByIdOnDaysRequest
    ) {
        return borrowBooksOnMillis(
                borrowBookByIdOnDaysRequest.bookId(),
                TimeUnit.DAYS.toMillis(borrowBookByIdOnDaysRequest.daysOfLoan()),
                1L,
                borrowBookByIdOnDaysRequest.bookOwnerSubject()
        );
    }

    public BookLoanResponse borrowBooksOnMillis(
            Long bookId,
            Long millisecondsOfLoan,
            Long quantity,
            String booksOwnerSubject
    ) {
        ResponseEntity<BookDTO> bookResponse = bookServiceClient.getBookById(bookId);
        if (bookResponse.getStatusCode() != HttpStatus.OK) {
            throw new BookServiceBookNotFoundException(bookId);
        }
        BookDTO bookDTO = Objects.requireNonNull(bookResponse.getBody());

        User user = userRepository.findBySubject(booksOwnerSubject)
                .orElseThrow(UserNotFoundException::new);
        LibraryBookQuantity libraryBookQuantity = libraryInventory.findBookQuantityByBookId(bookId);
        libraryInventory.removeBooksFrom(libraryBookQuantity, quantity);
        BookLoanEntity bookLoanEntity = Mapper.from(
                bookDTO,
                quantity,
                user,
                new Date(),
                millisecondsOfLoan
        );
        bookLoanRepository.save(bookLoanEntity);

        return Mapper.from(bookLoanEntity);
    }

    public void returnBook(ReturnBookRequest returnBookRequest){
        returnBooks(
                returnBookRequest.bookId(),
                1L,
                returnBookRequest.booksOwnerSubject()
        );
    }

    public void returnBooks(Long bookId, Long quantityToReturn, String booksOwnerSubject) {
        User user = userRepository.findBySubject(booksOwnerSubject)
                .orElseThrow(UserNotFoundException::new);
        BookLoanEntity bookLoan = bookLoanRepository.findByUserAndLoanBookQuantityBookId(user, bookId)
                .orElseThrow(() -> new BookLoanNotFoundException(bookId, user.getId()));

        LoanBookQuantity userLoanBookQuantity = bookLoan.getLoanBookQuantity();
        LibraryBookQuantity libraryBookQuantity = libraryInventory.findOrCreateBookQuantityByBookId(bookId);
        if(userLoanBookQuantity.getQuantity().equals(quantityToReturn)){
            libraryInventory.addBooksTo(libraryBookQuantity, quantityToReturn);
            bookLoanRepository.delete(bookLoan);
        } else {
            loanBookQuantityService.removeBooksFrom(userLoanBookQuantity, quantityToReturn);
        }
    }

    public void addBooksToLibrary(AddBooksToLibraryRequest addBooksToLibraryRequest){
        ResponseEntity<BookDTO> bookResponse = bookServiceClient.getBookById(addBooksToLibraryRequest.bookId());
        if (bookResponse.getStatusCode() != HttpStatus.OK) {
            throw new BookServiceBookNotFoundException(addBooksToLibraryRequest.bookId());
        }
        LibraryBookQuantity libraryBookQuantity
                = libraryInventory.findOrCreateBookQuantityByBookId(
                addBooksToLibraryRequest.bookId()
        );
        libraryInventory.addBooksTo(libraryBookQuantity, addBooksToLibraryRequest.quantity());
    }

}
