package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.exceptions.BookOutOfStockException;
import com.modsensoftware.library_service.models.BookQuantity;

public abstract class BookQuantityService<T extends BookQuantity> {

    public void removeBooksFrom(T bookQuantity, Long quantity){

        long finalBookQuantity = bookQuantity.getQuantity() - quantity;
        if(finalBookQuantity < 0){
            throw new BookOutOfStockException(
                    bookQuantity.getQuantity(), quantity, bookQuantity.getBookId());
        }
        bookQuantity.setQuantity(finalBookQuantity);

        saveBookQuantity(bookQuantity);
    }

    public void addBooksTo(T bookQuantity, Long quantity){
        bookQuantity.setQuantity(bookQuantity.getQuantity() + quantity);
        saveBookQuantity(bookQuantity);
    }

    public abstract void saveBookQuantity(T bookQuantity);

}
