package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.exceptions.BookOutOfStockException;
import com.modsensoftware.library_service.models.LoanBookQuantity;
import com.modsensoftware.library_service.repositories.LoanBookQuantityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LoanBookQuantityServiceTest {

    private LoanBookQuantityRepository loanBookQuantityRepository;
    private LoanBookQuantityService loanBookQuantityService;

    @BeforeEach
    void setUp() {
        loanBookQuantityRepository = mock(LoanBookQuantityRepository.class);
        loanBookQuantityService = new LoanBookQuantityService(loanBookQuantityRepository);
    }

    @Test
    void testSaveBookQuantity() {
        LoanBookQuantity bookQuantity = new LoanBookQuantity(1L, 10L);
        loanBookQuantityService.saveBookQuantity(bookQuantity);

        verify(loanBookQuantityRepository, times(1)).save(bookQuantity);
    }

    @Test
    void testRemoveBooksFromSuccess() {
        LoanBookQuantity bookQuantity = new LoanBookQuantity(1L, 10L);
        long quantityToRemove = 5L;

        loanBookQuantityService.removeBooksFrom(bookQuantity, quantityToRemove);

        assertEquals(5L, bookQuantity.getQuantity());
        verify(loanBookQuantityRepository, times(1)).save(bookQuantity);
    }

    @Test
    void testRemoveBooksFromOutOfStock() {
        LoanBookQuantity bookQuantity = new LoanBookQuantity(1L, 3L);
        long quantityToRemove = 5L;

        assertThrows(BookOutOfStockException.class, () -> {
            loanBookQuantityService.removeBooksFrom(bookQuantity, quantityToRemove);
        });
    }

    @Test
    void testAddBooksTo() {
        LoanBookQuantity bookQuantity = new LoanBookQuantity(1L, 10L);
        long quantityToAdd = 5L;

        loanBookQuantityService.addBooksTo(bookQuantity, quantityToAdd);

        assertEquals(15L, bookQuantity.getQuantity());
        verify(loanBookQuantityRepository, times(1)).save(bookQuantity);
    }
}