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

    @Column(name = "QUANTITY")
    private int quantity;

    // Relationship with Book
    @OneToOne
    @MapsId
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Book book;
}
