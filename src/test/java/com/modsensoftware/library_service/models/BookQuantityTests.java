package com.modsensoftware.library_service.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class BookQuantityTests {

    @Test
    void testBookQuantityConstructorAndGetters() {
        Long bookId = 1L;
        Long quantity = 10L;

        BookQuantity bookQuantity = new BookQuantity(bookId, quantity);

        Assertions.assertEquals(bookId, bookQuantity.getBookId());
        Assertions.assertEquals(quantity, bookQuantity.getQuantity());
    }

    @Test
    void testSetters() {
        BookQuantity bookQuantity = new BookQuantity();
        Long bookId = 2L;
        Long quantity = 5L;

        bookQuantity.setBookId(bookId);
        bookQuantity.setQuantity(quantity);

        Assertions.assertEquals(bookId, bookQuantity.getBookId());
        Assertions.assertEquals(quantity, bookQuantity.getQuantity());
    }
}