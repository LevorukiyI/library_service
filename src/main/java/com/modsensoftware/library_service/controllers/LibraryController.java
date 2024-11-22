package com.modsensoftware.library_service.controllers;

import com.modsensoftware.library_service.dtos.requests.AddBooksToLibraryRequest;
import com.modsensoftware.library_service.dtos.requests.BorrowBookOnDaysRequest;
import com.modsensoftware.library_service.dtos.requests.ReturnBookRequest;
import com.modsensoftware.library_service.dtos.responses.LibraryBookQuantityDTO;
import com.modsensoftware.library_service.dtos.responses.BookLoanResponse;
import com.modsensoftware.library_service.dtos.responses.ReturnBooksResponse;
import com.modsensoftware.library_service.exceptions.responses.ExceptionResponse;
import com.modsensoftware.library_service.services.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @Operation(
            summary = "get all books in library",
            description = "shows all books and their quantity in library inventory",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "you got all library book quantity **successfully**",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = LibraryBookQuantityDTO.class))
                    )
            ),
    })
    @PreAuthorize("hasAuthority('PERMISSION_GET_ALL_LIBRARY_BOOKS')")
    @GetMapping("/books")
    public ResponseEntity<List<LibraryBookQuantityDTO>> getAllLibraryBooks(){
        List<LibraryBookQuantityDTO> allBookQuantity = libraryService.getAllBookQuantity();
        return ResponseEntity.ok(allBookQuantity);
    }

    @Operation(
            summary = "add book to library",
            description = "It's necessary to add book to book_service before.<br>"
                    + "add book to library with bookId and quantity, that you specify.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "book moved from library to user loan **successfully**",
                    content = @Content(schema = @Schema(implementation = BookLoanResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "book_service client cant find book with your specified book id <br>",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "503", description = "it is not possible to connect to the book_service on which this service depends",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = AddBooksToLibraryRequest.class))
    )
    @PreAuthorize("hasAuthority('PERMISSION_ADD_BOOKS_TO_LIBRARY')")
    @PostMapping("/books")
    public ResponseEntity<LibraryBookQuantityDTO> addBooksToLibrary(
            @Valid @RequestBody AddBooksToLibraryRequest addBooksToLibraryRequest
    ) {
                LibraryBookQuantityDTO addedBooksToLibrary
                        = libraryService.addBooksToLibrary(addBooksToLibraryRequest);
        return ResponseEntity.status(HttpStatus.OK).body(addedBooksToLibrary);
    }

    @Operation(
            summary = "get book by id on user with subject",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "book moved from library to user loan **successfully**",
                    content = @Content(schema = @Schema(implementation = BookLoanResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "a specific error description will be passed to the ExceptionResponse. <br>"
                            + "book_service client can't find book with your specified book id <br>"
                            + "**OR** library inventory has no book with id, that you specified <br>",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "409", description = "in library inventory book quantity lesser then quantity, that you specified",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "503", description = "it is not possible to connect to the book_service on which this service depends",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = BorrowBookOnDaysRequest.class))
    )
    @PreAuthorize("hasAuthority('PERMISSION_BORROW_BOOK_ON_USER')")
    @PutMapping("/borrow-book-on-user/{bookId}")
    public ResponseEntity<BookLoanResponse> borrowBookByIdOnDays(
            @Parameter(description = "ID of the book to be borrowed", example = "12")
            @PathVariable @NotNull(message = "bookId can't be null") Long bookId,
            @Valid @RequestBody BorrowBookOnDaysRequest borrowBookOnDaysRequest
    ) {
        BookLoanResponse bookLoanResponse =
                libraryService.borrowBookOnDays(bookId, borrowBookOnDaysRequest);
        return ResponseEntity.ok(bookLoanResponse);
    }

    @Operation(
            summary = "return ONE book with id - bookID. From user with subject - booksOwnerSubject",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "book moved from user loan to library **successfully**",
                    content = @Content(schema = @Schema(implementation = ReturnBooksResponse.class))),
            @ApiResponse(responseCode = "404", description = "a specific error description will be passed to the ExceptionResponse. <br>"
                    + "There is no user with subject, that you specified <br>"
                    + "**OR** There is no book loan with loanId, that you specified <br>",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "409", description = "quantity of books in user loan is lesser " +
                    "then in quantity, that you trying to return.",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PreAuthorize("hasAuthority('PERMISSION_RETURN_BOOK_FOR_USER')")
    @PutMapping("/return-book")
    public ResponseEntity<ReturnBooksResponse> returnBook(
            @Valid @RequestBody ReturnBookRequest returnBookRequest
    ) {
        ReturnBooksResponse returnBooksResponse =
                libraryService.returnBook(returnBookRequest);
        return ResponseEntity.ok(returnBooksResponse);
    }
}
