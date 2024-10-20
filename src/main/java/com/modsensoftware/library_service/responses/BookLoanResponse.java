package com.modsensoftware.library_service.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record BookLoanResponse(

    @Schema(description = "Unique identifier for the book.", example = "12")
    Long bookId,

    @Schema(description = "Quantity of the book that is being loaned.", example = "3")
    Long quantity,

    @Schema(description = "Subject identifier of the user who has borrowed the book.")
    String userSubject,

    @Schema(description = "Date when the book was loaned out.", example = "2024-10-20T21:35:30.174Z")
    Date loanDate,

    @Schema(description = "Date when the book is expected to be returned.", example = "2024-17-20T21:35:30.174Z")
    Date returnDate
){
}
