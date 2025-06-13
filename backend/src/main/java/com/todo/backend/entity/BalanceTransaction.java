package com.todo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "balance_transactions")
public class BalanceTransaction {
    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BalanceTransactionType type;

    @Column(name = "amount", nullable = false)
    private int amount; // Can be positive or negative

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BalanceTransactionStatus status;

    // Default constructor
    public BalanceTransaction() {
        this.timestamp = LocalDateTime.now();
        this.status = BalanceTransactionStatus.COMPLETED; // Default to completed
    }

    // Constructor with parameters
    public BalanceTransaction(String id, String userId, BalanceTransactionType type, 
                             int amount, String description, int balanceAfter) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
        this.status = BalanceTransactionStatus.COMPLETED;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BalanceTransactionType getType() {
        return type;
    }

    public void setType(BalanceTransactionType type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(int balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public BalanceTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(BalanceTransactionStatus status) {
        this.status = status;
    }
}
