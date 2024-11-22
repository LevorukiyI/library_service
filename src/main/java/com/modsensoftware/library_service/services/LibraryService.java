package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.clients.BookServiceClient;
import com.modsensoftware.library_service.clients.dtos.BookDTO;
import com.modsensoftware.library_service.dtos.responses.LibraryBookQuantityDTO;
import com.modsensoftware.library_service.dtos.responses.ReturnBooksResponse;
import com.modsensoftware.library_service.exceptions.BookLoanNotFoundException;
import com.modsensoftware.library_service.exceptions.BookServiceBookNotFoundException;
import com.modsensoftware.library_service.exceptions.UserNotFoundException;
import com.modsensoftware.library_service.models.*;
import com.modsensoftware.library_service.repositories.BookLoanRepository;
import com.modsensoftware.library_service.repositories.LibraryBookQuantityRepository;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.dtos.requests.AddBooksToLibraryRequest;
import com.modsensoftware.library_service.dtos.requests.BorrowBookOnDaysRequest;
import com.modsensoftware.library_service.dtos.requests.ReturnBookRequest;
import com.modsensoftware.library_service.dtos.responses.BookLoanResponse;
import com.modsensoftware.library_service.utils.BookLoanMapper;
import com.modsensoftware.library_service.utils.BookQuantityMapper;
import com.modsensoftware.library_service.utils.ReturnBooksResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
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
    private final LibraryBookQuantityRepository libraryBookQuantityRepository;

    public List<LibraryBookQuantityDTO> getAllBookQuantity(){
        return libraryBookQuantityRepository.findAll().stream()
                .map(BookQuantityMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional
    public BookLoanResponse borrowBookOnDays(
            Long bookID,
            BorrowBookOnDaysRequest borrowBookOnDaysRequest
    ) {
        return borrowBooksOnMillis(
                bookID,
                TimeUnit.DAYS.toMillis(borrowBookOnDaysRequest.daysOfLoan()),
                1L,
                borrowBookOnDaysRequest.bookOwnerSubject()
        );
    }

    @Transactional
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
        BookLoanEntity bookLoanEntity = BookLoanMapper.toEntity(
                bookDTO,
                quantity,
                user,
                new Date(),
                millisecondsOfLoan
        );
        bookLoanRepository.save(bookLoanEntity);

        return BookLoanMapper.INSTANCE.toResponse(bookLoanEntity);
    }

    @Transactional
    public ReturnBooksResponse returnBook(ReturnBookRequest returnBookRequest){
        return returnBooks(
                returnBookRequest.loanId(),
                1L
        );
    }

    @Transactional
    public ReturnBooksResponse returnBooks(Long loanId, Long quantityToReturn) {
        BookLoanEntity bookLoan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new BookLoanNotFoundException(loanId));

        BookLoanEntity remainingUserBookLoan;

        LoanBookQuantity userLoanBookQuantity = bookLoan.getLoanBookQuantity();
        LibraryBookQuantity libraryBookQuantity = libraryInventory
                .findOrCreateBookQuantityByBookId(bookLoan.getLoanBookQuantity().getBookId());
        if(userLoanBookQuantity.getQuantity().equals(quantityToReturn)){
            bookLoanRepository.delete(bookLoan); //also deletes userLoanBookQuantity, because cascade
            remainingUserBookLoan = null;
        } else {
            loanBookQuantityService.removeBooksFrom(userLoanBookQuantity, quantityToReturn);
            remainingUserBookLoan = bookLoan;
        }
        LibraryBookQuantity addedBooksToLibrary =
                libraryInventory.addBooksTo(libraryBookQuantity, quantityToReturn);

        return ReturnBooksResponseMapper.toResponse(addedBooksToLibrary, remainingUserBookLoan);
    }

    @Transactional
    public LibraryBookQuantityDTO addBooksToLibrary(AddBooksToLibraryRequest addBooksToLibraryRequest){
        ResponseEntity<BookDTO> bookResponse = bookServiceClient.getBookById(addBooksToLibraryRequest.bookId());
        if (bookResponse.getStatusCode() != HttpStatus.OK) {
            throw new BookServiceBookNotFoundException(addBooksToLibraryRequest.bookId());
        }
        LibraryBookQuantity libraryBookQuantity
                = libraryInventory.findOrCreateBookQuantityByBookId(
                addBooksToLibraryRequest.bookId()
        );
        LibraryBookQuantity addedBookQuantity =
                libraryInventory.addBooksTo(libraryBookQuantity, addBooksToLibraryRequest.quantity());
        return BookQuantityMapper.INSTANCE.toDto(addedBookQuantity);
    }

}
