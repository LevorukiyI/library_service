package com.modsensoftware.library_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "response of the register user to whom the books will be issued")
public record RegisterUserResponse (
    @NotBlank(message = "user subject can't be blank")
    String subject
){
}