package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.exceptions.LibraryInventoryBookNotFoundException;
import com.modsensoftware.library_service.models.LibraryBookQuantity;
import com.modsensoftware.library_service.repositories.LibraryBookQuantityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LibraryInventoryService extends BookQuantityService<LibraryBookQuantity>{

    private final LibraryBookQuantityRepository libraryBookQuantityRepository;

    public LibraryBookQuantity findOrCreateBookQuantityByBookId(Long bookId){
        return this.findOrCreateBookQuantityByBookId(bookId, 0L);
    }

    public LibraryBookQuantity findOrCreateBookQuantityByBookId(Long bookId, Long quantity){
        return libraryBookQuantityRepository.findByBookId(bookId)
                .orElseGet(() -> new LibraryBookQuantity(bookId, quantity));
    }

    public LibraryBookQuantity findBookQuantityByBookId(Long bookId) {
        return libraryBookQuantityRepository.findByBookId(bookId)
                .orElseThrow(() -> new LibraryInventoryBookNotFoundException(bookId));
    }

    @Override
    @Transactional
    public void saveBookQuantity(LibraryBookQuantity bookQuantity) {
        libraryBookQuantityRepository.save(bookQuantity);
    }
}
