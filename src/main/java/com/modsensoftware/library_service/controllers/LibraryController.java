package com.modsensoftware.library_service.controllers;

import com.modsensoftware.library_service.requests.BorrowBookByIdOnDaysRequest;
import com.modsensoftware.library_service.requests.ReturnBookRequest;
import com.modsensoftware.library_service.responses.BookLoanResponse;
import com.modsensoftware.library_service.responses.ExceptionResponse;
import com.modsensoftware.library_service.services.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @Operation(
            summary = "get book by id on user with subject"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "book moved from library to user loan **successfully**",
                    content = @Content(schema = @Schema(implementation = BookLoanResponse.class))),
            @ApiResponse(responseCode = "404", description = "book_service client cant find book with your specified book id",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "here is no user with subject, that you specified",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "library inventory has no book with id, that you specified",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "503", description = "it is not possible to connect to the book_service on which this service depends",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "409", description = "in library inventory book quantity lesser then quantity, that you specified",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = BorrowBookByIdOnDaysRequest.class))
    )
    @PreAuthorize("hasAuthority('PERMISSION_BORROW_BOOK_ON_USER')")
    @PostMapping("/borrow-book-on-user")
    public ResponseEntity<BookLoanResponse> borrowBookByIdOnDays(
            @RequestBody BorrowBookByIdOnDaysRequest borrowBookByIdOnDaysRequest
    ) {
        BookLoanResponse bookLoanResponse =
                libraryService.borrowBookOnDays(borrowBookByIdOnDaysRequest);
        return ResponseEntity.ok(bookLoanResponse);
    }

    @Operation(
            summary = "return one book with id - bookID. From user with subject - booksOwnerSubject"
    )
    @PreAuthorize("hasAuthority('PERMISSION_RETURN_BOOK_FOR_USER')")
    @PostMapping("/return-book")
    public ResponseEntity<HttpStatus> returnBook(
            @RequestBody ReturnBookRequest returnBookRequest
    ) {
        libraryService.returnBook(returnBookRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
