package com.modsensoftware.library_service.utils;

import com.modsensoftware.library_service.clients.dtos.BookDTO;
import com.modsensoftware.library_service.models.BookLoanEntity;
import com.modsensoftware.library_service.models.LoanBookQuantity;
import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.dtos.responses.BookLoanResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Date;

class MapperTests {

    @Test
    void testFromBookLoanEntity() {
        User user = new User(1L, "user123", null);
        LoanBookQuantity loanBookQuantity = new LoanBookQuantity(12L, 3L);
        BookLoanEntity bookLoanEntity = new BookLoanEntity(1L, loanBookQuantity, user, new Date(), new Date());

        BookLoanResponse response = BookLoanMapper.INSTANCE.toResponse(bookLoanEntity);

        Assertions.assertEquals(12L, response.bookId());
        Assertions.assertEquals(3L, response.quantity());
        Assertions.assertEquals("user123", response.userSubject());
        Assertions.assertEquals(bookLoanEntity.getLoanDate(), response.loanDate());
        Assertions.assertEquals(bookLoanEntity.getReturnDate(), response.returnDate());
    }

    @Test
    void testFromBookDTO() {
        BookDTO bookDTO = new BookDTO(12L, "123456789", "Test Book", "Fiction", "A test book description", "Author Name");
        User user = new User(1L, "user123", null);
        Date loanDate = new Date();
        Long quantity = 3L;
        Long millisecondsOfLoan = 7 * 24 * 60 * 60 * 1000L; // 1 week in milliseconds

        BookLoanEntity bookLoanEntity = BookLoanMapper.toEntity(bookDTO, quantity, user, loanDate, millisecondsOfLoan);

        Assertions.assertNotNull(bookLoanEntity);
        Assertions.assertEquals(12L, bookLoanEntity.getLoanBookQuantity().getBookId());
        Assertions.assertEquals(3L, bookLoanEntity.getLoanBookQuantity().getQuantity());
        Assertions.assertEquals(user, bookLoanEntity.getUser());
        Assertions.assertEquals(loanDate, bookLoanEntity.getLoanDate());
        Assertions.assertEquals(new Date(loanDate.getTime() + millisecondsOfLoan), bookLoanEntity.getReturnDate());
    }
}
