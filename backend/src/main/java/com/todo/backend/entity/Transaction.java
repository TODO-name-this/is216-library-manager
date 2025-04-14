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

    // Relationships with Book
    @ManyToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Book book;

    // Relationships with User
    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private User user;

    // Relationships with ReturnDetails
    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL)
    private ReturnDetail returnDetail;
}
