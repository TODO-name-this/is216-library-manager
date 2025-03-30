package com.todo.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "INVENTORY")
@Data
public class Inventory {
    @Id
    @Column(name = "BOOK_ID")
    @NotNull
    private String bookId;

    @Column(name = "AMOUNT")
    private int amount;

    // Relationship with Book
    @OneToOne
    @MapsId
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")
    private Book book;
}
