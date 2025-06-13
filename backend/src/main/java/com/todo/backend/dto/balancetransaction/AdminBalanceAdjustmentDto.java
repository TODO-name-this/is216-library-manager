package com.todo.backend.dto.balancetransaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AdminBalanceAdjustmentDto {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Integer amount;
    
    @NotBlank(message = "Reason is required")
    private String reason;

    // Default constructor
    public AdminBalanceAdjustmentDto() {}

    // Constructor
    public AdminBalanceAdjustmentDto(String userId, Integer amount, String reason) {
        this.userId = userId;
        this.amount = amount;
        this.reason = reason;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
