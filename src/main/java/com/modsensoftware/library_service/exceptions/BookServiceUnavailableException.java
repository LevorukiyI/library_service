package com.modsensoftware.library_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class BookServiceUnavailableException extends RuntimeException {
    public BookServiceUnavailableException(Throwable cause) {
        super("it is not possible to connect to the book_service on which this service depends", cause);
    }
}