package com.modsensoftware.library_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookServiceBookNotFoundException extends RuntimeException{
    public BookServiceBookNotFoundException(Long bookId) {
        super("book_service client cant find book with id:" + bookId);
    }
}
