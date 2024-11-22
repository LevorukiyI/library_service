package com.modsensoftware.library_service.requests;

import com.modsensoftware.library_service.dtos.requests.ReturnBookRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReturnBookRequestTests {

    @Test
    void testReturnBookRequestConstructor() {
        Long loanId = 12L;

        ReturnBookRequest request = new ReturnBookRequest(loanId);

        assertEquals(loanId, request.loanId());
    }
}
