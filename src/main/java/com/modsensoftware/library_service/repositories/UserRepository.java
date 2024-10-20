package com.modsensoftware.library_service.repositories;

import com.modsensoftware.library_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySubject(String subject);
}
