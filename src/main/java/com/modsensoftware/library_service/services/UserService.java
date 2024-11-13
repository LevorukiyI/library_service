package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.requests.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void registerUser(RegisterUserRequest registerUserRequest){
        registerUser(registerUserRequest.getSubject());
    }

    @Transactional
    public void registerUser(String subject){
        User user = User.builder().subject(subject).build();
        userRepository.save(user);
    }
}