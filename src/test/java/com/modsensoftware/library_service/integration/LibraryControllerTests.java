package com.modsensoftware.library_service.integration;

import com.modsensoftware.library_service.config.ApplicationConfig;
import com.modsensoftware.library_service.config.security.SecurityConfiguration;
import com.modsensoftware.library_service.models.LibraryBookQuantity;
import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.repositories.BookLoanRepository;
import com.modsensoftware.library_service.repositories.LibraryBookQuantityRepository;
import com.modsensoftware.library_service.repositories.LoanBookQuantityRepository;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.requests.*;
import com.modsensoftware.library_service.responses.BookLoanResponse;
import com.modsensoftware.library_service.services.LibraryInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({SecurityConfiguration.class, ApplicationConfig.class})
@ActiveProfiles("test")
public class LibraryControllerTests {
    @Autowired
    private BookLoanRepository bookLoanRepository;

    @Autowired
    private LibraryBookQuantityRepository libraryBookQuantityRepository;

    @Autowired
    private LoanBookQuantityRepository loanBookQuantityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LibraryInventoryService libraryInventoryService;

    @Value("${book-service.base-url}")
    private String bookServiceBaseUrl;

    @Value("${book-service.secret-key}")
    private String bookServiceSecretKey;

    @Value("${application.security.this-service-secret-api-key}")
    private String thisServiceSecretKey;

    private final String thisServiceBaseUrl = "http://localhost:8081/library" ;

    @BeforeEach
    public void setUp() {
        this.tearDown();
    }

    public void tearDown() {
        bookLoanRepository.deleteAll();
        libraryBookQuantityRepository.deleteAll();
        loanBookQuantityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void testBorrowBookByIdOnDays() {
        Long bookId = getFirstBookIdFromBookService();
        this.addBooksToLibrary(bookId, 10L);

        User testUser = User.builder()
                .subject("testUser")
                .build();
        userRepository.save(testUser);

        long daysOfLoan = 5L;
        BorrowBookByIdOnDaysRequest borrowBookByIdOnDaysRequest
                = new BorrowBookByIdOnDaysRequest(bookId, daysOfLoan, testUser.getSubject());

        ResponseEntity<BookLoanResponse> responseEntity =
                this.sendRequestBorrowBookByIdOnDays(borrowBookByIdOnDaysRequest);
        BookLoanResponse actualResponse = responseEntity.getBody();

        Date actualResponseLoanDate = actualResponse.loanDate();
        BookLoanResponse expectedResponse =
                new BookLoanResponse(
                        bookId,
                        1L,
                        testUser.getSubject(),
                        actualResponseLoanDate,
                        new Date(actualResponseLoanDate.getTime() + TimeUnit.DAYS.toMillis(daysOfLoan))
                );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(expectedResponse.bookId(), actualResponse.bookId());
        assertEquals(expectedResponse.returnDate(), actualResponse.returnDate());
        assertEquals(expectedResponse.userSubject(), actualResponse.userSubject());
    }

    private ResponseEntity<BookLoanResponse> sendRequestBorrowBookByIdOnDays(
            BorrowBookByIdOnDaysRequest borrowBookByIdOnDaysRequest
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", thisServiceSecretKey);

        HttpEntity<BorrowBookByIdOnDaysRequest> requestEntity = new HttpEntity<>(borrowBookByIdOnDaysRequest, headers);

        return restTemplate.exchange(
                thisServiceBaseUrl + "/borrow-book-on-user",
                HttpMethod.POST,
                requestEntity,
                BookLoanResponse.class
        );
    }

    @Test
    @WithMockUser
    public void testReturnBook() {
        Long bookId = getFirstBookIdFromBookService();
        this.addBooksToLibrary(bookId, 10L);

        User testUser = User.builder()
                .subject("testUser")
                .build();
        userRepository.save(testUser);

        long daysOfLoan = 5L;
        BorrowBookByIdOnDaysRequest borrowBookByIdOnDaysRequest
                = new BorrowBookByIdOnDaysRequest( bookId, daysOfLoan, testUser.getSubject());

        this.sendRequestBorrowBookByIdOnDays(borrowBookByIdOnDaysRequest);

        ReturnBookRequest returnBookRequest = ReturnBookRequest.builder()
                .booksOwnerSubject(testUser.getSubject())
                .bookId(bookId)
                .build();

        ResponseEntity<?> responseEntity = sendRequestReturnBook(returnBookRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
    }

    public ResponseEntity<?> sendRequestReturnBook(ReturnBookRequest returnBookRequest){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", thisServiceSecretKey);

        HttpEntity<ReturnBookRequest> requestEntity = new HttpEntity<>(returnBookRequest, headers);

        return restTemplate.exchange(
                thisServiceBaseUrl + "/return-book",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );
    }

    public void addBooksToLibrary(Long bookId, long amountOfBooks) {
        LibraryBookQuantity libraryBookQuantity = LibraryBookQuantity.builder()
                .quantity(amountOfBooks)
                .bookId(bookId)
                .build();

        libraryInventoryService.saveBookQuantity(libraryBookQuantity);
    }

    private Long getFirstBookIdFromBookService(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", bookServiceSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                bookServiceBaseUrl + "/get-all-books",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().isEmpty()
        ) {
            throw new RuntimeException("Не удалось получить список книг");
        }
        return ((Number) response.getBody().get(0).get("id")).longValue();
    }
}