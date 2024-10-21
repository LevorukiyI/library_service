package com.modsensoftware.library_service.controllers;

import com.modsensoftware.library_service.requests.BorrowBookByIdOnDaysRequest;
import com.modsensoftware.library_service.requests.ReturnBookRequest;
import com.modsensoftware.library_service.responses.BookLoanResponse;
import com.modsensoftware.library_service.services.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibraryControllerTests {

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private LibraryController libraryController;

    @BeforeEach
    void setUp() {
        libraryService = Mockito.mock(LibraryService.class);
        libraryController = new LibraryController(libraryService);
    }

    @Test
    void testBorrowBookByIdOnDaysSuccess() {
        BorrowBookByIdOnDaysRequest request = new BorrowBookByIdOnDaysRequest(1L, 7L, "user123");
        Long bookId = 12L;
        Long quantity = 3L;
        String userSubject = "user123";
        Date loanDate = new Date();
        Date returnDate = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000); // 7 days from now

        BookLoanResponse response = new BookLoanResponse(bookId, quantity, userSubject, loanDate, returnDate);

        when(libraryService.borrowBookOnDays(request)).thenReturn(response);

        ResponseEntity<BookLoanResponse> result = libraryController.borrowBookByIdOnDays(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void testBorrowBookByIdOnDaysBookNotFound() {
        BorrowBookByIdOnDaysRequest request = new BorrowBookByIdOnDaysRequest(1L, 7L, "user123");

        when(libraryService.borrowBookOnDays(request)).thenThrow(new RuntimeException("Book not found"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            libraryController.borrowBookByIdOnDays(request);
        });

        assertEquals("Book not found", thrown.getMessage());
    }

    @Test
    void testReturnBookSuccess() {
        ReturnBookRequest returnRequest = new ReturnBookRequest(1L, "user123");

        ResponseEntity<HttpStatus> result = libraryController.returnBook(returnRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(libraryService, times(1)).returnBook(returnRequest);
    }

    @Test
    void testReturnBookLoanNotFound() {
        ReturnBookRequest returnRequest = new ReturnBookRequest(1L, "user123");

        doThrow(new RuntimeException("Loan not found")).when(libraryService).returnBook(returnRequest);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            libraryController.returnBook(returnRequest);
        });

        assertEquals("Loan not found", thrown.getMessage());
    }
}
