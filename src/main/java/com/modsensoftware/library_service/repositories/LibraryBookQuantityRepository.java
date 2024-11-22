package com.modsensoftware.library_service.repositories;

import com.modsensoftware.library_service.models.LibraryBookQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryBookQuantityRepository extends JpaRepository<LibraryBookQuantity, Long>{
    List<LibraryBookQuantity> findAll();
    Optional<LibraryBookQuantity> findByBookId(Long bookId);
}
