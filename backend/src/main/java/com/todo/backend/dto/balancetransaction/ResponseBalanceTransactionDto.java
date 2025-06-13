package com.todo.backend.dto.balancetransaction;

import com.todo.backend.entity.BalanceTransactionType;
import java.time.LocalDateTime;

public class ResponseBalanceTransactionDto {
    
    private String id;
    private String userId;
    private String userName;
    private String adminId;
    private String adminName;
    private BalanceTransactionType transactionType;
    private int amount;
    private int previousBalance;
    private int newBalance;
    private String reason;
    private LocalDateTime createdAt;

    // Default constructor
    public ResponseBalanceTransactionDto() {}

    // Full constructor
    public ResponseBalanceTransactionDto(String id, String userId, String userName, 
                                       String adminId, String adminName, 
                                       BalanceTransactionType transactionType, 
                                       int amount, int previousBalance, int newBalance, 
                                       String reason, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.adminId = adminId;
        this.adminName = adminName;
        this.transactionType = transactionType;
        this.amount = amount;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.reason = reason;
        this.createdAt = createdAt;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public BalanceTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(BalanceTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(int previousBalance) {
        this.previousBalance = previousBalance;
    }

    public int getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(int newBalance) {
        this.newBalance = newBalance;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
