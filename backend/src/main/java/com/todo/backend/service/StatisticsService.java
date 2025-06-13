package com.todo.backend.service;

import com.todo.backend.dao.StatisticsRepository;
import com.todo.backend.dto.statistics.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatisticsService {
    
    private final StatisticsRepository statisticsRepository;    public StatisticsDataDto getStatistics() {
        return getStatistics("year", null, null, null);
    }
    
    public StatisticsDataDto getStatistics(String period, Integer year, Integer month, Integer quarter) {
        LocalDate now = LocalDate.now();
        int currentYear = year != null ? year : now.getYear();
        int currentMonth = month != null ? month : now.getMonthValue();
        int currentQuarter = quarter != null ? quarter : ((now.getMonthValue() - 1) / 3) + 1;
        
        // Calculate date range based on period
        LocalDate startDate;
        LocalDate endDate;
        
        switch (period.toLowerCase()) {
            case "week" -> {
                startDate = now.minusDays(now.getDayOfWeek().getValue() - 1); // Start of current week
                endDate = startDate.plusDays(6); // End of current week
            }
            case "month" -> {
                startDate = LocalDate.of(currentYear, currentMonth, 1);
                endDate = startDate.plusMonths(1).minusDays(1);
            }
            case "quarter" -> {
                int startMonth = (currentQuarter - 1) * 3 + 1;
                startDate = LocalDate.of(currentYear, startMonth, 1);
                endDate = startDate.plusMonths(3).minusDays(1);
            }
            case "year" -> {
                startDate = LocalDate.of(currentYear, 1, 1);
                endDate = LocalDate.of(currentYear, 12, 31);
            }
            default -> {
                // Default to current year
                startDate = LocalDate.of(currentYear, 1, 1);
                endDate = LocalDate.of(currentYear, 12, 31);
            }
        }
        
        // Convert monthly stats from Object[] to DTOs
        List<Object[]> monthlyData = statisticsRepository.getMonthlyStats(currentYear);
        List<MonthlyStatDto> monthlyStats = monthlyData.stream()
                .map(row -> {
                    int monthNum = (Integer) row[0];
                    String monthName = getMonthName(monthNum);
                    int transactions = (Integer) row[1];
                    long revenue = (Long) row[2];
                    return new MonthlyStatDto(monthName, transactions, revenue);
                })
                .toList();

        return StatisticsDataDto.builder()
                // Basic counts
                .totalUsers(statisticsRepository.getTotalUsers())
                .totalBooks(statisticsRepository.getTotalBooks())
                .totalTransactions(statisticsRepository.getTotalTransactionsInPeriod(startDate, endDate))
                
                // Detailed revenue breakdown
                .grossRevenue(statisticsRepository.getGrossRevenue())           // Total deposits collected
                .netRevenue(statisticsRepository.getNetRevenue())               // Actual profit/loss
                .totalPenalties(statisticsRepository.getTotalPenalties())       // Penalty fees collected
                .totalRefunds(statisticsRepository.getTotalRefunds())           // Money refunded to users
                .totalRevenue(statisticsRepository.getGrossRevenue())           // Legacy field (same as gross)
                
                // Transaction status
                .activeTransactions(statisticsRepository.getActiveTransactions())
                .overdueTransactions(statisticsRepository.getOverdueTransactions())
                
                // Detailed breakdowns
                .popularBooks(statisticsRepository.getPopularBooksInPeriod(5, startDate, endDate))
                .monthlyStats(monthlyStats)
                .userActivity(statisticsRepository.getUserActivity())
                .bookCondition(statisticsRepository.getBookConditionStats())
                .build();
    }
    
    private String getMonthName(int monthNum) {
        return switch (monthNum) {
            case 1 -> "Jan";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Apr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Aug";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dec";
            default -> "Unknown";
        };
    }
}
/**
    * Get library statistics for different time periods with detailed revenue breakdown
    *
    * Revenue Types Explained:
    * 1. Gross Revenue: Total money collected from users when they borrow books (book deposits)
    * 2. Net Revenue: Actual profit/loss = penalties collected - refunds given back to users
    * 3. Total Penalties: Sum of all penalty fees (late fees, damage fees, additional fees)
    * 4. Total Refunds: Sum of all money given back to users when returning books
    *
    * Example:
    * - User borrows book worth 100,000 VND (pays 100,000 VND deposit)
    * - User returns book 5 days late with 10,000 VND penalty
    * - User gets back 90,000 VND (deposit - penalty)
    * - Gross Revenue: +100,000 VND, Net Revenue: +10,000 VND, Penalties: +10,000 VND, Refunds: +90,000 VND
**/
