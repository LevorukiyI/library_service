package com.modsensoftware.library_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "subject of the user to whom the books will be issued")
public record RegisterUserRequest (
        @NotBlank(message = "user subject can't be blank")
        String subject
){
}
