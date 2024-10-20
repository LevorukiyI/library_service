package com.modsensoftware.library_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookOutOfStockException extends RuntimeException {
    public BookOutOfStockException(Long availableBookQuantity, Long removingBookQuantity, Long bookId) {
        super(String.format(
                "in BookQuantity object book quantity is: %o; which is lesser then: %o; bookId: %o",
                availableBookQuantity ,
                removingBookQuantity,
                bookId
        ));
    }
}
