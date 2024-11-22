package com.modsensoftware.library_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "added book quantity to library")
public record LibraryBookQuantityDTO(
        @Schema(description = "id of added books.",
                example = "1")
        @NotNull(message = "bookId can't be null")
        Long bookId,

        @Min(0)
        @Schema(description = "quantity of added books.", example = "10")
        @NotNull(message = "quantity can't be null")
        Long quantity
) {
}
