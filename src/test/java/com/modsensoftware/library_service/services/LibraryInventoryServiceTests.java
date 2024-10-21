package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.exceptions.LibraryInventoryBookNotFoundException;
import com.modsensoftware.library_service.models.LibraryBookQuantity;
import com.modsensoftware.library_service.repositories.LibraryBookQuantityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibraryInventoryServiceTests {

    private LibraryBookQuantityRepository libraryBookQuantityRepository;

    private LibraryInventoryService libraryInventoryService;

    @BeforeEach
    void setUp() {
        libraryBookQuantityRepository = Mockito.mock(LibraryBookQuantityRepository.class);
        libraryInventoryService = new LibraryInventoryService(libraryBookQuantityRepository);
    }

    @Test
    void testFindOrCreateBookQuantityByBookIdExisting() {
        Long bookId = 1L;
        LibraryBookQuantity existingBookQuantity = new LibraryBookQuantity(bookId, 10L);
        when(libraryBookQuantityRepository.findByBookId(bookId)).thenReturn(Optional.of(existingBookQuantity));

        LibraryBookQuantity result = libraryInventoryService.findOrCreateBookQuantityByBookId(bookId);

        assertEquals(existingBookQuantity, result);
    }

    @Test
    void testFindOrCreateBookQuantityByBookIdNew() {
        Long bookId = 1L;
        when(libraryBookQuantityRepository.findByBookId(bookId)).thenReturn(Optional.empty());

        LibraryBookQuantity result = libraryInventoryService.findOrCreateBookQuantityByBookId(bookId);

        assertEquals(bookId, result.getBookId());
        assertEquals(0L, result.getQuantity());
        verify(libraryBookQuantityRepository, times(1)).findByBookId(bookId);
    }

    @Test
    void testFindBookQuantityByBookIdExisting() {
        Long bookId = 1L;
        LibraryBookQuantity existingBookQuantity = new LibraryBookQuantity(bookId, 10L);
        when(libraryBookQuantityRepository.findByBookId(bookId)).thenReturn(Optional.of(existingBookQuantity));

        LibraryBookQuantity result = libraryInventoryService.findBookQuantityByBookId(bookId);

        assertEquals(existingBookQuantity, result);
        verify(libraryBookQuantityRepository, times(1)).findByBookId(bookId);
    }

    @Test
    void testFindBookQuantityByBookIdNotFound() {
        Long bookId = 1L;
        when(libraryBookQuantityRepository.findByBookId(bookId)).thenReturn(Optional.empty());

        assertThrows(LibraryInventoryBookNotFoundException.class, () -> {
            libraryInventoryService.findBookQuantityByBookId(bookId);
        });
    }

    @Test
    void testSaveBookQuantity() {
        LibraryBookQuantity bookQuantity = new LibraryBookQuantity(1L, 10L);
        libraryInventoryService.saveBookQuantity(bookQuantity);

        verify(libraryBookQuantityRepository, times(1)).save(bookQuantity);
    }
}