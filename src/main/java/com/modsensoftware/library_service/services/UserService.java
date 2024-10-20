package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.requests.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void registerUser(RegisterUserRequest registerUserRequest){
        registerUser(registerUserRequest.getSubject());
    }

    public void registerUser(String subject){
        User user = User.builder().subject(subject).build();
        userRepository.save(user);
    }
}