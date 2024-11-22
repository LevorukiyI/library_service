package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.dtos.responses.BookLoanResponse;
import com.modsensoftware.library_service.exceptions.UserAlreadyExistsException;
import com.modsensoftware.library_service.exceptions.UserNotFoundException;
import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.repositories.BookLoanRepository;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.dtos.requests.RegisterUserRequest;
import com.modsensoftware.library_service.utils.BookLoanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BookLoanRepository bookLoanRepository;

    @Transactional
    public void registerUser(RegisterUserRequest registerUserRequest){
        registerUser(registerUserRequest.subject());
    }

    @Transactional
    public void registerUser(String subject){
        if(userRepository.existsBySubject(subject)){
            throw new UserAlreadyExistsException("user with such subject already were registered");
        }
        User user = User.builder().subject(subject).build();
        userRepository.save(user);
    }

    public List<BookLoanResponse> getUserLoans(String loanOwnerSubject){
        User user = userRepository.findBySubject(loanOwnerSubject)
                .orElseThrow(UserNotFoundException::new);
        return bookLoanRepository.findAllByUser(user).stream()
                .map(BookLoanMapper.INSTANCE::toResponse)
                .toList();
    }
}