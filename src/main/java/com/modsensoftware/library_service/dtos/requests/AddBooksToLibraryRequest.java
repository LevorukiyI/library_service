package com.modsensoftware.library_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to add books to library service. You have to create book in book_service before")
public record AddBooksToLibraryRequest(
        @Schema(description = "id of book that you want to add to the library. this bookId must be in book_service",
                example = "1")
        @NotNull(message = "bookId can't be null")
        Long bookId,

        @Min(0)
        @Schema(description = "quantity of books, that you want to add.", example = "10")
        @NotNull(message = "quantity can't be null")
        Long quantity
) {
}
