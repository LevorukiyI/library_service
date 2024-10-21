package com.modsensoftware.library_service.exceptions;

import com.modsensoftware.library_service.exceptions.handlers.DatabaseExceptionHandler;
import com.modsensoftware.library_service.exceptions.handlers.GlobalExceptionHandler;
import com.modsensoftware.library_service.responses.ExceptionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerTests {

    private final DatabaseExceptionHandler databaseExceptionHandler = new DatabaseExceptionHandler();
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleDataAccessException() {
        DataAccessException ex = new DataAccessException("Database error") {};

        ResponseEntity<ExceptionResponse> response = databaseExceptionHandler.handleDataAccessException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Database Error An error occurred while accessing the database: Database error", response.getBody().errorMessage());
    }

    @Test
    void testHandleAllExceptions() {
        RuntimeException runtimeException = new RuntimeException("Generic error");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleAllExceptions(runtimeException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Generic error", response.getBody().errorMessage());
    }

    @Test
    void testHandleSpecificExceptionWithResponseStatus() {
        BookLoanNotFoundException exception = new BookLoanNotFoundException(1L, 2L);

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleAllExceptions(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no book loan with bookId: 1; and userId: 2;", response.getBody().errorMessage());
    }
}