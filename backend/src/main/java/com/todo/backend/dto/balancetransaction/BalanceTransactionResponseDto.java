package com.todo.backend.dto.balancetransaction;

import com.todo.backend.entity.BalanceTransactionType;
import com.todo.backend.entity.BalanceTransactionStatus;

public class BalanceTransactionResponseDto {
    private String id;
    private BalanceTransactionType type;
    private int amount;
    private String description;
    private String timestamp; // ISO format for frontend
    private int balanceAfter;
    private BalanceTransactionStatus status;

    // Default constructor
    public BalanceTransactionResponseDto() {}

    // Constructor with parameters
    public BalanceTransactionResponseDto(String id, BalanceTransactionType type, int amount, 
                                       String description, String timestamp, int balanceAfter, 
                                       BalanceTransactionStatus status) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.balanceAfter = balanceAfter;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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
