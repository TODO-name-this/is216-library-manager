package com.todo.backend.entity;

public enum BookCopyStatus {
    /// Is available in-store for borrowing, including those that are reserved
    AVAILABLE,
    /// Is currently borrowed by a user, either due or not
    BORROWED,
    /// For internal use only, not available for borrowing
    UNAVAILABLE,
    /// Is damaged, not available for usage
    DAMAGED,
    /// Lost copies
    LOST
}
