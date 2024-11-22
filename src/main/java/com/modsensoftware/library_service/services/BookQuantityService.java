package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.exceptions.BookOutOfStockException;
import com.modsensoftware.library_service.models.BookQuantity;
import org.springframework.transaction.annotation.Transactional;

public abstract class BookQuantityService<T extends BookQuantity> {

    @Transactional
    public void removeBooksFrom(T bookQuantity, Long quantity){

        long finalBookQuantity = bookQuantity.getQuantity() - quantity;
        if(finalBookQuantity < 0){
            throw new BookOutOfStockException(
                    bookQuantity.getQuantity(), quantity, bookQuantity.getBookId());
        }
        bookQuantity.setQuantity(finalBookQuantity);

        saveBookQuantity(bookQuantity);
    }

    @Transactional
    public T addBooksTo(T bookQuantity, Long quantity){
        bookQuantity.setQuantity(bookQuantity.getQuantity() + quantity);
        saveBookQuantity(bookQuantity);
        return bookQuantity;
    }

    @Transactional
    public abstract void saveBookQuantity(T bookQuantity);

}
