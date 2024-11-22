package com.modsensoftware.library_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookLoanNotFoundException extends RuntimeException {
    public BookLoanNotFoundException(Long loanId) {
        super(String.format("There is no book loan with loanId: %o;", loanId));
    }
}
