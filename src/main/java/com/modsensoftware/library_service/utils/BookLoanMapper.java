package com.modsensoftware.library_service.utils;

import com.modsensoftware.library_service.clients.dtos.BookDTO;
import com.modsensoftware.library_service.dtos.responses.BookLoanResponse;
import com.modsensoftware.library_service.models.BookLoanEntity;
import com.modsensoftware.library_service.models.LoanBookQuantity;
import com.modsensoftware.library_service.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.Date;

@Mapper
public interface BookLoanMapper {
    BookLoanMapper INSTANCE = Mappers.getMapper(BookLoanMapper.class);

    @Mapping(source = "id", target = "loanId")
    @Mapping(source = "loanBookQuantity.bookId", target = "bookId")
    @Mapping(source = "loanBookQuantity.quantity", target = "quantity")
    @Mapping(source = "user.subject", target = "userSubject")
    BookLoanResponse toResponse(BookLoanEntity bookLoanEntity);

     static BookLoanEntity toEntity(
            BookDTO bookDTO,
            Long quantity,
            User user,
            Date loanDate,
            Long millisecondsOfLoan
    ) {
        return BookLoanEntity.builder()
                .loanBookQuantity(new LoanBookQuantity(bookDTO.id(), quantity))
                .user(user)
                .loanDate(loanDate)
                .returnDate(new Date(loanDate.getTime() + millisecondsOfLoan))
                .build();
    }
}
