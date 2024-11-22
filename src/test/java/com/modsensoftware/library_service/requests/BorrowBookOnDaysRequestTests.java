package com.modsensoftware.library_service.requests;

import com.modsensoftware.library_service.dtos.requests.BorrowBookOnDaysRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BorrowBookOnDaysRequestTests {

    @Test
    void testBorrowBookByIdOnDaysRequestConstructor() {
        Long daysOfLoan = 7L;
        String bookOwnerSubject = "user123";

        BorrowBookOnDaysRequest request = new BorrowBookOnDaysRequest(daysOfLoan, bookOwnerSubject);

        assertEquals(daysOfLoan, request.daysOfLoan());
        assertEquals(bookOwnerSubject, request.bookOwnerSubject());
    }
}
