package com.modsensoftware.library_service.clients.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO representing a book with relevant details.")
public record BookDTO(
        @NotNull(message = "bookId can't be null")
        @Schema(description = "Unique identifier for the book.", example = "12")
        Long id,

        @NotBlank(message = "isbn can't be blank")
        @Schema(description = "The ISBN of the book, which must be a non-blank string.", example = "978-3-16-148410-0")
        String isbn,

        @NotBlank(message = "title of book can't be blank")
        @Schema(description = "The title of the book, which must be a non-blank string.", example = "Effective Java")
        String title,

        @Schema(description = "The genre of the book.", example = "Programming")
        String genre,

        @Schema(description = "A brief description of the book.", example = "This book provides best practices for programming in Java.")
        String description,

        @NotBlank(message = "author field can't be blank")
        @Schema(description = "The author of the book, which must be a non-blank string.", example = "Joshua Bloch")
        String author
) {
}
