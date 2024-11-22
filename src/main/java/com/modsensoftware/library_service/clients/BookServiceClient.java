package com.modsensoftware.library_service.clients;

import com.modsensoftware.library_service.clients.dtos.BookDTO;
import com.modsensoftware.library_service.exceptions.BookServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class BookServiceClient {
    @Value("${book-service.base-url}")
    private String bookServiceBaseUrl;

    @Value("${book-service.secret-key}")
    private String bookServiceSecretKey;

    private final RestTemplate restTemplate;

    public ResponseEntity<BookDTO> getBookById(Long bookId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", bookServiceSecretKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(
                    bookServiceBaseUrl + "/" + bookId,
                    HttpMethod.GET,
                    entity,
                    BookDTO.class
            );
        } catch (RestClientException e) {
            throw new BookServiceUnavailableException(e);
        }
    }

}
