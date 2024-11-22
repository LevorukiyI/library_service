package com.modsensoftware.library_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BorrowBookOnDaysRequest(
    @Schema(description = "Number of days to borrow the book", example = "7")
    @NotNull(message = "daysOfLoan can't be null")
    Long daysOfLoan,

    @Schema(description = "subject of the user to whom the book will be issued")
    @NotBlank(message = "bookOwnerSubject can't be blank")
    String bookOwnerSubject
){
}
