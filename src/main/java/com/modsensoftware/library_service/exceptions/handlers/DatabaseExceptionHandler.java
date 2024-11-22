package com.modsensoftware.library_service.exceptions.handlers;

import com.modsensoftware.library_service.exceptions.responses.ExceptionResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DatabaseExceptionHandler {
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponse> handleDataAccessException(DataAccessException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                "Database Error An error occurred while accessing the database: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }
}
