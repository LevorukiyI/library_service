package com.modsensoftware.library_service.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "loan_book_quantity")
public class LoanBookQuantity extends BookQuantity {
    public LoanBookQuantity(Long bookId, Long quantity){
        super();
        this.setBookId(bookId);
        this.setQuantity(quantity);
    }
}