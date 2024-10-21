package com.modsensoftware.library_service.controllers;

import com.modsensoftware.library_service.requests.RegisterUserRequest;
import com.modsensoftware.library_service.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void testRegisterUserSuccess() {
        RegisterUserRequest request = new RegisterUserRequest("user123");

        ResponseEntity<HttpStatus> result = userController.registerUser(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService, times(1)).registerUser(request);
    }

    @Test
    void testRegisterUserException() {
        RegisterUserRequest request = new RegisterUserRequest("user123");

        doThrow(new RuntimeException("Registration failed")).when(userService).registerUser(request);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userController.registerUser(request);
        });

        assertEquals("Registration failed", thrown.getMessage());
    }
}