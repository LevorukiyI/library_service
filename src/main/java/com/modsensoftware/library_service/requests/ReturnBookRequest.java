package com.modsensoftware.library_service.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ReturnBookRequest {
    private Long bookId;
    private String booksOwnerSubject;
}
