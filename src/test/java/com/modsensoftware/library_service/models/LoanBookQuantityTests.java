package com.modsensoftware.library_service.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoanBookQuantityTests {

    @Test
    void testLoanBookQuantityConstructor() {
        Long bookId = 3L;
        Long quantity = 8L;

        LoanBookQuantity loanBookQuantity = new LoanBookQuantity(bookId, quantity);

        Assertions.assertEquals(bookId, loanBookQuantity.getBookId());
        Assertions.assertEquals(quantity, loanBookQuantity.getQuantity());
    }

    @Test
    void testLoanBookQuantityDefaultConstructor() {
        LoanBookQuantity loanBookQuantity = new LoanBookQuantity();

        Assertions.assertNull(loanBookQuantity.getBookId());
        Assertions.assertNull(loanBookQuantity.getQuantity());
    }
}