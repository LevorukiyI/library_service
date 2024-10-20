package com.modsensoftware.library_service.repositories;

import com.modsensoftware.library_service.models.LoanBookQuantity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanBookQuantityRepository extends JpaRepository<LoanBookQuantity, Long> {
}
