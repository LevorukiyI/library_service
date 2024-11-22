package com.modsensoftware.library_service.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.modsensoftware.library_service.annotations.ContainerTest;
import com.modsensoftware.library_service.clients.dtos.BookDTO;
import com.modsensoftware.library_service.config.ApplicationConfig;
import com.modsensoftware.library_service.config.security.SecurityConfiguration;
import com.modsensoftware.library_service.controllers.UserController;
import com.modsensoftware.library_service.dtos.requests.BorrowBookOnDaysRequest;
import com.modsensoftware.library_service.dtos.responses.BookLoanResponse;
import com.modsensoftware.library_service.models.LibraryBookQuantity;
import com.modsensoftware.library_service.models.User;
import com.modsensoftware.library_service.repositories.BookLoanRepository;
import com.modsensoftware.library_service.repositories.UserRepository;
import com.modsensoftware.library_service.security.filters.ApiKeyAuthenticationFilter;
import com.modsensoftware.library_service.services.LibraryInventoryService;
import com.modsensoftware.library_service.services.LibraryService;
import com.modsensoftware.library_service.services.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, ApplicationConfig.class})
@ActiveProfiles("test")
@ContainerTest
@WireMockTest(httpPort = 8083)
@DirtiesContext
public class UserControllerIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookLoanRepository bookLoanRepository;

    @Value("${book-service.secret-key}")
    private String bookServiceSecretKey;

    @Value("${application.security.this-service-secret-api-key}")
    private String thisServiceSecretKey;

    @Value("${book-service.base-url}")
    private String bookServiceBaseUrl;

    @Autowired
    private ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Autowired
    private LibraryInventoryService libraryInventoryService;

    @Autowired
    private LibraryService libraryService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private static final int WIREMOCK_PORT = 8083;
    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .addFilters(apiKeyAuthenticationFilter)
                .build();

        wireMockServer = new WireMockServer();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
        wireMockServer.start();
    }

    @AfterEach
    public void tearDown() {
        bookLoanRepository.deleteAll();
        userRepository.deleteAll();

        wireMockServer.stop();
    }

    @SneakyThrows
    @Test
    public void testGetUserLoans() {
        Long bookId = 12L;
        this.addBooksToLibrary(bookId, 10L);
        User testUser = User.builder()
                .subject("testUser")
                .build();
        userRepository.save(testUser);
        long daysOfLoan = 5L;

        BorrowBookOnDaysRequest borrowBookOnDaysRequest
                = new BorrowBookOnDaysRequest(daysOfLoan, testUser.getSubject());

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

        BookLoanResponse actualResponse = libraryService.borrowBookOnDays(bookId, borrowBookOnDaysRequest);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/{subject}/loans",  testUser.getSubject())
                        .header("x-api-key", thisServiceSecretKey)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();
        String responseJson = mvcResult.getResponse().getContentAsString();

        List<BookLoanResponse> bookLoanResponses
                = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertEquals(bookLoanResponses.get(0).userSubject(), borrowBookOnDaysRequest.bookOwnerSubject());
    }

    public void addBooksToLibrary(Long bookId, long amountOfBooks) {
        LibraryBookQuantity libraryBookQuantity = LibraryBookQuantity.builder()
                .quantity(amountOfBooks)
                .bookId(bookId)
                .build();

        libraryInventoryService.saveBookQuantity(libraryBookQuantity);
    }
}
