package com.modsensoftware.library_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record BookLoanResponse(
        @Schema(description = "Unique identifier for the book loan.", example = "3")
        @NotNull(message = "loanId can't be null")
        Long loanId,

        @Schema(description = "Unique identifier for the book.", example = "12")
        @NotNull(message = "bookId can't be null")
        Long bookId,

        @Schema(description = "Quantity of the book that is being loaned.", example = "3")
        @NotNull(message = "quantity can't be null")
        Long quantity,

        @Schema(description = "Subject identifier of the user who has borrowed the book.")
        @NotBlank(message = "bookOwnerSubject can't be blank")
        String userSubject,

        @Schema(description = "Date when the book was loaned out.", example = "2024-10-20T21:35:30.174Z")
        @NotNull(message = "loanDate can't be null")
        Date loanDate,

        @Schema(description = "Date when the book is expected to be returned.", example = "2024-17-20T21:35:30.174Z")
        @NotNull(message = "returnDate can't be null")
        Date returnDate
) {
}
