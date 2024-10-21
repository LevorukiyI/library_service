package com.modsensoftware.library_service.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReturnBookRequestTests {

    @Test
    void testReturnBookRequestConstructor() {
        Long bookId = 12L;
        String booksOwnerSubject = "user123";

        ReturnBookRequest request = new ReturnBookRequest(bookId, booksOwnerSubject);

        assertEquals(bookId, request.bookId());
        assertEquals(booksOwnerSubject, request.booksOwnerSubject());
    }
}
