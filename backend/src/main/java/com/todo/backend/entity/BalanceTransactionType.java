package com.todo.backend.entity;

public enum BalanceTransactionType {
    DEPOSIT,        // Manual deposit by admin or user
    WITHDRAWAL,     // Manual withdrawal by admin
    BOOK_RENTAL,    // User borrows book (negative amount)
    PENALTY_FEE,    // Late fee or damage fee (negative amount)
    REFUND          // Book return refund (positive amount)
}
