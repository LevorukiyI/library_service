package com.modsensoftware.library_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder
public class BookQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long bookId;

    @Column(nullable = false)
    @Min(0)
    private Long quantity;

    public BookQuantity(Long bookId, Long quantity){
        this.bookId = bookId;
        this.quantity = quantity;
    }
}
