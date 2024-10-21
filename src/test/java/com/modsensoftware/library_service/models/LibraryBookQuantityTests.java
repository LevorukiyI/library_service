package com.modsensoftware.library_service.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LibraryBookQuantityTests {

    @Test
    void testLibraryBookQuantityConstructor() {
        Long bookId = 4L;
        Long quantity = 12L;

        LibraryBookQuantity libraryBookQuantity = new LibraryBookQuantity(bookId, quantity);

        Assertions.assertEquals(bookId, libraryBookQuantity.getBookId());
        Assertions.assertEquals(quantity, libraryBookQuantity.getQuantity());
    }

    @Test
    void testLibraryBookQuantityDefaultConstructor() {
        LibraryBookQuantity libraryBookQuantity = new LibraryBookQuantity();

        Assertions.assertNull(libraryBookQuantity.getBookId());
        Assertions.assertNull(libraryBookQuantity.getQuantity());
    }
}
