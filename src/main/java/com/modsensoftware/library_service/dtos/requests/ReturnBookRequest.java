package com.modsensoftware.library_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReturnBookRequest (
        @Schema(description = "id of the loan, which you want to close", example = "3")
        @NotNull(message = "loanId can't be null")
        Long loanId
){
}
