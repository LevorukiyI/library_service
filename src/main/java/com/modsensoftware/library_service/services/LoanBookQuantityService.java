package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.models.LoanBookQuantity;
import com.modsensoftware.library_service.repositories.LoanBookQuantityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanBookQuantityService extends BookQuantityService<LoanBookQuantity> {

    private final LoanBookQuantityRepository loanBookQuantityRepository;

    @Override
    @Transactional
    public void saveBookQuantity(LoanBookQuantity bookQuantity) {
        loanBookQuantityRepository.save(bookQuantity);
    }
}
