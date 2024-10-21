package com.modsensoftware.library_service.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BorrowBookByIdOnDaysRequestTests {

    @Test
    void testBorrowBookByIdOnDaysRequestConstructor() {
        Long bookId = 12L;
        Long daysOfLoan = 7L;
        String bookOwnerSubject = "user123";

        BorrowBookByIdOnDaysRequest request = new BorrowBookByIdOnDaysRequest(bookId, daysOfLoan, bookOwnerSubject);

        assertEquals(bookId, request.bookId());
        assertEquals(daysOfLoan, request.daysOfLoan());
        assertEquals(bookOwnerSubject, request.bookOwnerSubject());
    }
}
