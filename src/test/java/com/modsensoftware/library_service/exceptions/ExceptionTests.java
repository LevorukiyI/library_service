package com.modsensoftware.library_service.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTests {

    @Test
    void testBookLoanNotFoundException() {
        Long loanId = 1L;

        BookLoanNotFoundException exception = new BookLoanNotFoundException(loanId);

        assertEquals("There is no book loan with loanId: 1;", exception.getMessage());
    }

    @Test
    void testBookOutOfStockException() {
        Long availableQuantity = 3L;
        Long removingQuantity = 5L;
        Long bookId = 1L;

        BookOutOfStockException exception = new BookOutOfStockException(availableQuantity, removingQuantity, bookId);

        assertEquals("in BookQuantity object book quantity is: 3; which is lesser then: 5; bookId: 1", exception.getMessage());
    }

    @Test
    void testBookServiceBookNotFoundException() {
        Long loanId = 1L;

        BookServiceBookNotFoundException exception = new BookServiceBookNotFoundException(loanId);

        assertEquals("book_service client cant find book with id:1", exception.getMessage());
    }

    @Test
    void testLibraryInventoryBookNotFoundException() {
        Long loanId = 1L;

        LibraryInventoryBookNotFoundException exception = new LibraryInventoryBookNotFoundException(loanId);

        assertEquals("library inventory has no book with id: 1", exception.getMessage());
    }

    @Test
    void testUserNotFoundException() {
        UserNotFoundException exception = new UserNotFoundException();

        assertEquals("there is no user with such subject or username", exception.getMessage());
    }

    @Test
    void testBookServiceUnavailableException() {
        Throwable cause = new RuntimeException("Service not available");

        BookServiceUnavailableException exception = new BookServiceUnavailableException(cause);

        assertEquals("it is not possible to connect to the book_service on which this service depends", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
