package com.modsensoftware.library_service.utils;

import com.modsensoftware.library_service.dtos.BookDTO;
import com.modsensoftware.library_service.models.BookLoanEntity;
import com.modsensoftware.library_service.models.LoanBookQuantity;
import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.responses.BookLoanResponse;

import java.util.Date;

public class Mapper {

    public static BookLoanResponse from(
            BookLoanEntity bookLoanEntity
    ) {
        return new BookLoanResponse(
                bookLoanEntity.getLoanBookQuantity().getBookId(),
                bookLoanEntity.getLoanBookQuantity().getQuantity(),
                bookLoanEntity.getUser().getSubject(),
                bookLoanEntity.getLoanDate(),
                bookLoanEntity.getReturnDate()
        );
    }

    public static BookLoanEntity from(
            BookDTO bookDTO,
            Long quantity,
            User user,
            Date loanDate,
            Long millisecondsOfLoan
    ) {
        return BookLoanEntity.builder()
                .loanBookQuantity(new LoanBookQuantity(bookDTO.getId(), quantity))
                .user(user)
                .loanDate(loanDate)
                .returnDate(new Date(loanDate.getTime() + millisecondsOfLoan))
                .build();
    }
}
