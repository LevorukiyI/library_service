package com.modsensoftware.library_service.utils;

import com.modsensoftware.library_service.dtos.responses.LibraryBookQuantityDTO;
import com.modsensoftware.library_service.dtos.responses.BookLoanResponse;
import com.modsensoftware.library_service.dtos.responses.ReturnBooksResponse;
import com.modsensoftware.library_service.models.BookLoanEntity;
import com.modsensoftware.library_service.models.LibraryBookQuantity;

public interface ReturnBooksResponseMapper {
    static ReturnBooksResponse toResponse(
            LibraryBookQuantity libraryBookQuantity,
            BookLoanEntity remainingBookLoan
    ){
        LibraryBookQuantityDTO libraryBookQuantityDTO =
                BookQuantityMapper.INSTANCE.toDto(libraryBookQuantity);
        BookLoanResponse remainingBookLoanResponse =
                remainingBookLoan != null ? BookLoanMapper.INSTANCE.toResponse(remainingBookLoan) : null;
        return new ReturnBooksResponse(
                libraryBookQuantityDTO,
                remainingBookLoanResponse
        );
    }
}
