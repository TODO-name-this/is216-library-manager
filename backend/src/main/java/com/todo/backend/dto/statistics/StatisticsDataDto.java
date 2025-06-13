package com.todo.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class StatisticsDataDto {
    // Basic counts
    private long totalUsers;                    // Total number of users in system
    private long totalBooks;                    // Total number of book titles in system
    private long totalTransactions;             // Total number of borrowing transactions
    
    // Revenue breakdown - explained in detail
    private long grossRevenue;                  // Total money collected from book deposits (what users pay to borrow)
    private long netRevenue;                    // Actual profit/loss (penalties collected - refunds given back to users)
    private long totalPenalties;                // Total penalty fees collected from users (late fees, damage fees, etc.)
    private long totalRefunds;                  // Total money refunded to users when returning books
    
    // Legacy field for backward compatibility (same as grossRevenue)
    @Deprecated
    private long totalRevenue;                  // VND - Use grossRevenue instead
    
    // Transaction status
    private long activeTransactions;            // Books currently borrowed (not returned yet)
    private long overdueTransactions;           // Books that are past due date
    
    // Detailed breakdowns
    private List<PopularBookDto> popularBooks;  // Most borrowed books with their stats
    private List<MonthlyStatDto> monthlyStats;  // Monthly transaction and revenue data
    private List<UserActivityDto> userActivity; // User count by role (USER, ADMIN, LIBRARIAN)
    private List<BookConditionDto> bookCondition; // Book condition statistics (NEW, GOOD, WORN, DAMAGED)
}
