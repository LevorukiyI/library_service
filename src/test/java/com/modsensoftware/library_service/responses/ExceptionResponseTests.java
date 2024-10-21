package com.modsensoftware.library_service.responses;

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