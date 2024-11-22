package com.modsensoftware.library_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response for returning books to the library.")
public record ReturnBooksResponse(
        @Schema(description = "Information about added books to the library.")
        LibraryBookQuantityDTO addedBooks,

        @Schema(description = "Information about the remaining loan for the book.")
        BookLoanResponse remainingLoan
) {
}
