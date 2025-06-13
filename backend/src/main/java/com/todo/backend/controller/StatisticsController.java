package com.todo.backend.controller;

import com.todo.backend.dto.statistics.StatisticsDataDto;
import com.todo.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;    
    /**
    * Get library statistics with detailed revenue breakdown
    * Revenue Explanation:
    * - grossRevenue: Total money collected from users (what they pay to borrow books)
    * - netRevenue: Actual profit/loss (penalties collected - refunds given back)
    * - totalPenalties: Money collected from late fees, damage fees, etc.
    * - totalRefunds: Money given back to users when they return books
    *
    * @param period Time period: "week", "month", "quarter", "year" (default: "year")
    * @param year Specific year (default: current year)
    * @param month Specific month 1-12 (only for month period)
    * @param quarter Specific quarter 1-4 (only for quarter period)
    * @return Statistics data with revenue breakdown
    */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping
    public ResponseEntity<?> getStatistics(
            @RequestParam(required = false, defaultValue = "year") String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer quarter) {
        try {
            StatisticsDataDto statistics = statisticsService.getStatistics(period, year, month, quarter);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching statistics: " + e.getMessage());
        }
    }
}
