package com.modsensoftware.library_service.requests;

import io.swagger.v3.oas.annotations.media.Schema;

public record BorrowBookByIdOnDaysRequest (
    @Schema(description = "ID of the book to be borrowed", example = "12")
    Long bookId,

    @Schema(description = "Number of days to borrow the book", example = "7")
    Long daysOfLoan,

    @Schema(description = "subject of the user to whom the book will be issued")
    String bookOwnerSubject
){
}
