package com.modsensoftware.library_service.responses;

import com.modsensoftware.library_service.exceptions.responses.ExceptionResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionResponseTests {

    @Test
    void testExceptionResponseConstructor() {
        String errorMessage = "An error occurred";

        ExceptionResponse response = new ExceptionResponse(errorMessage);

        assertEquals(errorMessage, response.errorMessage());
    }
}