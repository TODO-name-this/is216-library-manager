package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "TRANSACTION")
@Data
public class Transaction {
    @Id
    @Column(name = "ID")
    @NotBlank(message = "Transaction ID is required")
    private String id;

    @Column(name = "BORROW_DATE")
    @NotBlank(message = "Borrow date is required")
    private String borrowDate;

    @Column(name = "DUE_DATE")
    @NotBlank(message = "Due date is required")
    private String dueDate;

    @Column(name = "USER_ID")
    @NotBlank(message = "[Transaction] User ID is required")
    private String userId;

    // Relationship with User
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private User user;

    // Relationship with TransactionDetail
    @JsonIgnore
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionDetail> transactionDetails;
}
