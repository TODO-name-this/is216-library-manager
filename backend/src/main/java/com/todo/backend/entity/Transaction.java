package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "TRANSACTION")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "BORROW_DATE")
    private LocalDate borrowDate;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "BOOK_COPY_ID")
    private String bookCopyId;

    @Column(name = "RETURNED_DATE")
    private LocalDate returnedDate;

    // Relationship with User
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private User user;

    // Relationship with BookCopy
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_COPY_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private BookCopy bookCopy;

    // Relationship with TransactionDetail (one-to-zero-or-one)
    @JsonIgnore
    @OneToOne(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private TransactionDetail transactionDetail;
}
