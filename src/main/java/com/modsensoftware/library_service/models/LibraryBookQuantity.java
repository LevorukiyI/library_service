package com.modsensoftware.library_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "library_book_quantity")
@SuperBuilder
@NoArgsConstructor
public class LibraryBookQuantity extends BookQuantity {
    public LibraryBookQuantity(Long bookId, Long quantity){
        super(bookId, quantity);
    }
}