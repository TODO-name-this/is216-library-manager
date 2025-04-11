package com.todo.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "TRANSACTION")
@Data
public class Transaction {
    @Id
    @Column(name = "ID")
    @NotNull
    private String id;

    @Column(name = "BORROW_DATE")
    private String borrowDate;

    @Column(name = "DUE_DATE")
    private String dueDate;

    @Column(name = "RETURN_DATE")
    private String returnDate;

    @Column(name = "QUANTITY")
    private int quantity;

    @Column(name = "BOOK_ID")
    private String bookId;

    @Column(name = "USER_ID")
    private String userId;

    // Relationships with Book and User
    @ManyToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private User user;
}
