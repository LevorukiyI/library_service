package com.modsensoftware.library_service.controllers;

import com.modsensoftware.library_service.dtos.requests.RegisterUserRequest;
import com.modsensoftware.library_service.dtos.responses.BookLoanResponse;
import com.modsensoftware.library_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/register")
    public ResponseEntity<HttpStatus> registerUser(
            @Valid @RequestBody RegisterUserRequest registerUserRequest
    ) {
        userService.registerUser(registerUserRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(
            summary = "get user loans by subject",
            security = @SecurityRequirement(name = "Bearer"),
            parameters = @Parameter(name = "subject", description = "loans owner subject", required = true)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User loans returned **successfully**"),
            @ApiResponse(responseCode = "404", description = "there is no user with subject, that you specified"),
    })
    @PreAuthorize("hasAuthority('PERMISSION_GET_LOANS_BY_USER')")
    @GetMapping("/{subject}/loans")
    public ResponseEntity<List<BookLoanResponse>> getUserLoans(
            @PathVariable @NotBlank(message = "user subject can't be blank") String subject
    ){
        List<BookLoanResponse> bookLoanResponses = userService.getUserLoans(subject);
        return ResponseEntity.ok(bookLoanResponses);
    }

}
