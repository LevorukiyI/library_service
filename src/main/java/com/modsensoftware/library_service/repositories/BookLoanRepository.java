package com.modsensoftware.library_service.repositories;

import com.modsensoftware.library_service.models.BookLoanEntity;
import com.modsensoftware.library_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoanEntity, Long> {
    List<BookLoanEntity> findAllByUser(User user);
    Optional<BookLoanEntity> findById(Long id);
}
