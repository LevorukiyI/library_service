package com.modsensoftware.library_service.services;

import com.modsensoftware.library_service.models.LoanBookQuantity;
import com.modsensoftware.library_service.repositories.LoanBookQuantityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanBookQuantityService extends BookQuantityService<LoanBookQuantity> {

    private final LoanBookQuantityRepository loanBookQuantityRepository;

    @Override
    public void saveBookQuantity(LoanBookQuantity bookQuantity) {
        loanBookQuantityRepository.save(bookQuantity);
    }
}
