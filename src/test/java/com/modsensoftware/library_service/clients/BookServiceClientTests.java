package com.modsensoftware.library_service.clients;

import com.modsensoftware.library_service.clients.dtos.BookDTO;
import com.modsensoftware.library_service.exceptions.BookServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookServiceClientTests {

    private BookServiceClient bookServiceClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws Exception {
        restTemplate = mock(RestTemplate.class);
        bookServiceClient = new BookServiceClient(restTemplate);

        setFieldValue("bookServiceBaseUrl", "http://localhost:8080/book-service");
        setFieldValue("bookServiceSecretKey", "secret-key");
    }

    private void setFieldValue(String fieldName, String value) throws Exception {
        Field field = BookServiceClient.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(bookServiceClient, value);
    }

    @Test
    void testGetBookByIdSuccess() {
        Long bookId = 1L;
        BookDTO expectedBook = new BookDTO(bookId, "Dummy Book", "123456789", "Fiction", "A dummy book description", "Author Name");
        ResponseEntity<BookDTO> responseEntity = ResponseEntity.ok(expectedBook);

        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(BookDTO.class)
        )).thenReturn(responseEntity);

        ResponseEntity<BookDTO> result = bookServiceClient.getBookById(bookId);

        assertEquals(expectedBook, result.getBody());
        verify(restTemplate).exchange(
                "http://localhost:8080/book-service/" + bookId,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders() {{
                    set("x-api-key", "secret-key");
                }}),
                BookDTO.class
        );
    }

    @Test
    void testGetBookByIdServiceUnavailable() {
        Long bookId = 1L;
        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(BookDTO.class)
        )).thenThrow(new RestClientException("Service Unavailable"));

        assertThrows(BookServiceUnavailableException.class,
                () -> bookServiceClient.getBookById(bookId));
    }
}