package com.modsensoftware.library_service.controllers;

import com.modsensoftware.library_service.requests.RegisterUserRequest;
import com.modsensoftware.library_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "register user in database",
            security = @SecurityRequirement(name = "x-api-key")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
    })
    @PreAuthorize("hasAuthority('PERMISSION_REGISTER_LIBRARY_SERVICE_USER')")
    @PostMapping("/register-user")
    public ResponseEntity<HttpStatus> registerUser(
            @RequestBody RegisterUserRequest registerUserRequest
    ) {
        userService.registerUser(registerUserRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
