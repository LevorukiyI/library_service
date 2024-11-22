package com.modsensoftware.library_service.exceptions.handlers;

import com.modsensoftware.library_service.exceptions.responses.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllExceptions(RuntimeException runtimeException){
        return createResponseEntity(runtimeException);
    }

    private ResponseEntity<ExceptionResponse> createResponseEntity(RuntimeException runtimeException) {
        HttpStatus status = getResponseStatus(runtimeException);
        ExceptionResponse exceptionResponse = new ExceptionResponse(runtimeException.getMessage());
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    private HttpStatus getResponseStatus(RuntimeException runtimeException) {
        ResponseStatus responseStatus = runtimeException.getClass().getAnnotation(ResponseStatus.class);
        return responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
    }

}