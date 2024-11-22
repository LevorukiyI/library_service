package com.modsensoftware.library_service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.modsensoftware.library_service.annotations.ContainerTest;
import com.modsensoftware.library_service.clients.dtos.BookDTO;
import com.modsensoftware.library_service.config.ApplicationConfig;
import com.modsensoftware.library_service.config.security.SecurityConfiguration;
import com.modsensoftware.library_service.controllers.LibraryController;
import com.modsensoftware.library_service.dtos.requests.BorrowBookOnDaysRequest;
import com.modsensoftware.library_service.dtos.requests.ReturnBookRequest;
import com.modsensoftware.library_service.dtos.responses.LibraryBookQuantityDTO;
import com.modsensoftware.library_service.models.LibraryBookQuantity;
import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.repositories.BookLoanRepository;
import com.modsensoftware.library_service.repositories.LibraryBookQuantityRepository;
import com.modsensoftware.library_service.repositories.LoanBookQuantityRepository;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.dtos.responses.BookLoanResponse;
import com.modsensoftware.library_service.security.filters.ApiKeyAuthenticationFilter;
import com.modsensoftware.library_service.services.LibraryInventoryService;
import com.modsensoftware.library_service.services.LibraryService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, ApplicationConfig.class})
@ActiveProfiles("test")
@ContainerTest
@WireMockTest(httpPort = 8083)
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

    @Value("${book-service.secret-key}")
    private String bookServiceSecretKey;

    @Value("${application.security.this-service-secret-api-key}")
    private String thisServiceSecretKey;

    private final String thisServiceBaseUrl = "http://localhost:8082/library" ;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private static final int WIREMOCK_PORT = 8083;
    private WireMockServer wireMockServer;

    @Value("${book-service.base-url}")
    private String bookServiceBaseUrl;

    @Autowired
    private ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Autowired
    private LibraryService libraryService;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new LibraryController(libraryService))
                .addFilters(apiKeyAuthenticationFilter)
                .build();

        wireMockServer = new WireMockServer();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
        wireMockServer.start();
    }

    @AfterEach
    public void tearDown() {
        bookLoanRepository.deleteAll();
        libraryBookQuantityRepository.deleteAll();
        userRepository.deleteAll();

        wireMockServer.stop();
    }

    @SneakyThrows
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
        BorrowBookOnDaysRequest borrowBookOnDaysRequest
                = new BorrowBookOnDaysRequest(daysOfLoan, testUser.getSubject());

        BookLoanResponse actualResponse = this.sendRequestBorrowBookByIdOnDays(bookId, borrowBookOnDaysRequest);

        assert actualResponse != null;
        Date actualResponseLoanDate = actualResponse.loanDate();
        BookLoanResponse expectedResponse =
                new BookLoanResponse(
                        actualResponse.loanId(),
                        bookId,
                        1L,
                        testUser.getSubject(),
                        actualResponseLoanDate,
                        new Date(actualResponseLoanDate.getTime() + TimeUnit.DAYS.toMillis(daysOfLoan))
                );

        assertEquals(expectedResponse.bookId(), actualResponse.bookId());
        assertEquals(expectedResponse.returnDate(), actualResponse.returnDate());
        assertEquals(expectedResponse.userSubject(), actualResponse.userSubject());
    }

    public BookLoanResponse sendRequestBorrowBookByIdOnDays(
            Long bookId,
            BorrowBookOnDaysRequest borrowBookOnDaysRequest
    ) throws Exception {
        BookDTO bookDTO = new BookDTO(bookId, "978-3-16-148410-0", "Effective Java", "Programming",
                "This book provides best practices for programming in Java.", "Joshua Bloch");
        String jsonResponse = objectMapper.writeValueAsString(bookDTO);

        stubFor(WireMock.get(urlEqualTo("/books/" + bookId))
                .withHeader("x-api-key", equalTo(bookServiceSecretKey))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)
                ));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put("/library/borrow-book-on-user/{bookId}", bookId)
                        .header("x-api-key", thisServiceSecretKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowBookOnDaysRequest)))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(responseJson, BookLoanResponse.class);
    }

    @SneakyThrows
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

        BorrowBookOnDaysRequest borrowBookOnDaysRequest
                = new BorrowBookOnDaysRequest(daysOfLoan, testUser.getSubject());

        BookLoanResponse actualResponse = this.sendRequestBorrowBookByIdOnDays(bookId, borrowBookOnDaysRequest);

        ReturnBookRequest returnBookRequest = new ReturnBookRequest(
                actualResponse.loanId()
        );

       sendRequestReturnBook(returnBookRequest);
    }

    public void sendRequestReturnBook(ReturnBookRequest returnBookRequest) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/library/return-book")
                        .header("x-api-key", thisServiceSecretKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(returnBookRequest)))
                .andExpect(status().isOk());
    }

    private Long getFirstBookIdFromBookService() throws JsonProcessingException {
        return LibraryControllerTests.getFirstBookIdFromBookService(
                this.objectMapper,
                bookServiceSecretKey,
                bookServiceBaseUrl,
                restTemplate
        );
    }

    public static Long getFirstBookIdFromBookService(
            ObjectMapper objectMapper,
            String bookServiceSecretKey,
            String bookServiceBaseUrl,
            RestTemplate restTemplate
    ) throws JsonProcessingException {
        BookDTO bookDTO = new BookDTO(12L, "978-3-16-148410-0", "Effective Java", "Programming",
                "This book provides best practices for programming in Java.", "Joshua Bloch");
        List<BookDTO> responseBody = new ArrayList<>();
        responseBody.add(bookDTO);
        String jsonResponse = objectMapper.writeValueAsString(responseBody);

        stubFor(WireMock.get(urlEqualTo("/books"))
                .withHeader("x-api-key", equalTo(bookServiceSecretKey))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)
                ));

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", bookServiceSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                bookServiceBaseUrl,
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

    @Test
    public void getAllLibraryBooksTest() throws Exception {
        LibraryBookQuantity libraryBookQuantity1 = LibraryBookQuantity.builder()
                .quantity(10L)
                .bookId(1L)
                .build();
        LibraryBookQuantity libraryBookQuantity2 = LibraryBookQuantity.builder()
                .quantity(20L)
                .bookId(2L)
                .build();
        libraryInventoryService.saveBookQuantity(libraryBookQuantity1);
        libraryInventoryService.saveBookQuantity(libraryBookQuantity2);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/library/books")
                        .header("x-api-key", thisServiceSecretKey)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        List<LibraryBookQuantityDTO> libraryBookQuantityDTOS
                = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertEquals(libraryBookQuantityDTOS.get(0).quantity(), libraryBookQuantity1.getQuantity());
        assertEquals(libraryBookQuantityDTOS.get(0).bookId(), libraryBookQuantity1.getBookId());
        assertEquals(libraryBookQuantityDTOS.get(1).quantity(), libraryBookQuantity2.getQuantity());
        assertEquals(libraryBookQuantityDTOS.get(1).bookId(), libraryBookQuantity2.getBookId());
    }

    public void addBooksToLibrary(Long bookId, long amountOfBooks) {
        LibraryBookQuantity libraryBookQuantity = LibraryBookQuantity.builder()
                .quantity(amountOfBooks)
                .bookId(bookId)
                .build();

        libraryInventoryService.saveBookQuantity(libraryBookQuantity);
    }
}