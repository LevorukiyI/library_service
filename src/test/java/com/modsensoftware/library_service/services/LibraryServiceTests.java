package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.clients.BookServiceClient;
import com.modsensoftware.library_service.dtos.BookDTO;
import com.modsensoftware.library_service.exceptions.BookLoanNotFoundException;
import com.modsensoftware.library_service.exceptions.BookServiceBookNotFoundException;
import com.modsensoftware.library_service.exceptions.UserNotFoundException;
import com.modsensoftware.library_service.models.BookLoanEntity;
import com.modsensoftware.library_service.models.LibraryBookQuantity;
import com.modsensoftware.library_service.models.LoanBookQuantity;
import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.repositories.BookLoanRepository;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.requests.BorrowBookByIdOnDaysRequest;
import com.modsensoftware.library_service.requests.ReturnBookRequest;
import com.modsensoftware.library_service.responses.BookLoanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LibraryServiceTests {

    private BookServiceClient bookServiceClient;
    private LibraryInventoryService libraryInventory;
    private LoanBookQuantityService loanBookQuantityService;
    private BookLoanRepository bookLoanRepository;
    private UserRepository userRepository;
    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        bookServiceClient = Mockito.mock(BookServiceClient.class);
        libraryInventory = Mockito.mock(LibraryInventoryService.class);
        loanBookQuantityService = Mockito.mock(LoanBookQuantityService.class);
        bookLoanRepository = Mockito.mock(BookLoanRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        libraryService = new LibraryService(bookServiceClient, libraryInventory, loanBookQuantityService, bookLoanRepository, userRepository);
    }

    @Test
    void testBorrowBookOnDaysSuccess() {
        Long bookId = 1L;
        Long daysOfLoan = 7L;
        String userSubject = "user123";

        BorrowBookByIdOnDaysRequest request = new BorrowBookByIdOnDaysRequest(bookId, daysOfLoan, userSubject);
        BookDTO bookDTO = new BookDTO(bookId, "Test Book", "123456789", "Fiction", "A test book", "Author");
        User user = User.builder()
                .subject(userSubject)
                .build();

        LibraryBookQuantity libraryBookQuantity = new LibraryBookQuantity(bookId, 10L);
        BookLoanEntity bookLoanEntity = new BookLoanEntity();

        when(bookServiceClient.getBookById(bookId)).thenReturn(ResponseEntity.ok(bookDTO));
        when(userRepository.findBySubject(userSubject)).thenReturn(Optional.of(user));
        when(libraryInventory.findBookQuantityByBookId(bookId)).thenReturn(libraryBookQuantity);
        when(bookLoanRepository.save(any(BookLoanEntity.class))).thenReturn(bookLoanEntity);

        BookLoanResponse response = libraryService.borrowBookOnDays(request);

        assertNotNull(response);
        verify(libraryInventory, times(1)).removeBooksFrom(libraryBookQuantity, 1L);
        verify(bookLoanRepository, times(1)).save(any(BookLoanEntity.class));
    }

    @Test
    void testBorrowBookOnDaysBookNotFound() {
        Long bookId = 1L;
        Long daysOfLoan = 7L;
        String userSubject = "user123";

        BorrowBookByIdOnDaysRequest request = new BorrowBookByIdOnDaysRequest(bookId, daysOfLoan, userSubject);

        when(bookServiceClient.getBookById(bookId)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        assertThrows(BookServiceBookNotFoundException.class, () -> {
            libraryService.borrowBookOnDays(request);
        });
    }

    @Test
    void testReturnBookSuccess() {
        Long bookId = 1L;
        String userSubject = "user123";

        ReturnBookRequest returnRequest = new ReturnBookRequest(bookId, userSubject);
        BookLoanEntity bookLoanEntity = new BookLoanEntity();
        LoanBookQuantity userLoanBookQuantity = new LoanBookQuantity(1L, 1L);
        bookLoanEntity.setLoanBookQuantity(userLoanBookQuantity);

        User user = User.builder()
                .subject(userSubject)
                .build();
        LibraryBookQuantity libraryBookQuantity = new LibraryBookQuantity(bookId, 5L);

        when(userRepository.findBySubject(userSubject)).thenReturn(Optional.of(user));
        when(bookLoanRepository.findByUserAndLoanBookQuantityBookId(user, bookId)).thenReturn(Optional.of(bookLoanEntity));
        when(libraryInventory.findOrCreateBookQuantityByBookId(bookId)).thenReturn(libraryBookQuantity);

        libraryService.returnBook(returnRequest);

        verify(bookLoanRepository, times(1)).delete(bookLoanEntity);
    }

    @Test
    void testReturnBookLoanNotFound() {
        Long bookId = 1L;
        String userSubject = "user123";

        ReturnBookRequest returnRequest = new ReturnBookRequest(bookId, userSubject);
        User user = User.builder()
                .subject(userSubject)
                .build();

        when(userRepository.findBySubject(userSubject)).thenReturn(Optional.of(user));
        when(bookLoanRepository.findByUserAndLoanBookQuantityBookId(user, bookId)).thenReturn(Optional.empty());

        assertThrows(BookLoanNotFoundException.class, () -> {
            libraryService.returnBook(returnRequest);
        });
    }

    @Test
    void testReturnBookUserNotFound() {
        Long bookId = 1L;
        String userSubject = "user123";

        ReturnBookRequest returnRequest = new ReturnBookRequest(bookId, userSubject);

        when(userRepository.findBySubject(userSubject)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            libraryService.returnBook(returnRequest);
        });
    }
}