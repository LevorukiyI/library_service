package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.repositories.BookLoanRepository;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.dtos.requests.RegisterUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTests {

    private UserRepository userRepository;
    private UserService userService;
    private BookLoanRepository bookLoanRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        bookLoanRepository = Mockito.mock(BookLoanRepository.class);
        userService = new UserService(userRepository, bookLoanRepository);
    }

    @Test
    void testRegisterUserWithRequest() {
        RegisterUserRequest request = new RegisterUserRequest("user123");

        userService.registerUser(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserWithSubject() {
        String subject = "user123";

        userService.registerUser(subject);

        verify(userRepository, times(1)).save(any(User.class));
    }
}
