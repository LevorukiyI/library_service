package com.modsensoftware.library_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LibraryInventoryBookNotFoundException extends RuntimeException{
    public LibraryInventoryBookNotFoundException(Long bookId) {
        super("library inventory has no book with id: " + bookId);
    }
}
