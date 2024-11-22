package com.modsensoftware.library_service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String subject;

    @OneToMany(mappedBy = "user",
            fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<BookLoanEntity> bookLoanEntities;
}
