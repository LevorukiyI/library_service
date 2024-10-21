package com.modsensoftware.library_service.requests;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReturnBookRequest (
        @Schema(description = "id of the book, that you want to return", example = "12")
    Long bookId,

    @Schema(description = "subject of user who owns the book, with id bookId")
    String booksOwnerSubject
){
}
