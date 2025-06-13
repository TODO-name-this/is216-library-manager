package com.todo.backend.dao;

import com.todo.backend.dto.statistics.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import java.time.LocalDate;
import java.util.List;

@Repository
public class StatisticsRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // Total counts
    public long getTotalUsers() {
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM User u");
        return (Long) query.getSingleResult();
    }
    
    public long getTotalBooks() {
        Query query = entityManager.createQuery("SELECT COUNT(bt) FROM BookTitle bt");
        return (Long) query.getSingleResult();
    }
    
    public long getTotalTransactions() {
        Query query = entityManager.createQuery("SELECT COUNT(t) FROM Transaction t");
        return (Long) query.getSingleResult();
    }

    // Revenue calculation (gross revenue from all transactions)
    public long getTotalRevenue() {
        Query query = entityManager.createQuery("""
            SELECT COALESCE(SUM(bc.bookTitle.price), 0)
            FROM Transaction t
            JOIN BookCopy bc ON t.bookCopyId = bc.id
            """);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
    
    // Active transactions (not returned)
    public long getActiveTransactions() {
        Query query = entityManager.createQuery("SELECT COUNT(t) FROM Transaction t WHERE t.returnedDate IS NULL");
        return (Long) query.getSingleResult();
    }
    
    // Overdue transactions
    public long getOverdueTransactions() {
        Query query = entityManager.createQuery("""
            SELECT COUNT(t) FROM Transaction t 
            WHERE t.returnedDate IS NULL 
            AND t.dueDate < CURRENT_DATE
            """);
        return (Long) query.getSingleResult();
    }
    
    // Popular books by borrow count
    @SuppressWarnings("unchecked")
    public List<PopularBookDto> getPopularBooks(int limit) {
        Query query = entityManager.createQuery("""
            SELECT new com.todo.backend.dto.statistics.PopularBookDto(
                bt.title,
                CAST(COUNT(t) AS int),
                CAST(COALESCE(SUM(bt.price), 0) AS long)
            )
            FROM Transaction t
            JOIN BookCopy bc ON t.bookCopyId = bc.id
            JOIN BookTitle bt ON bc.bookTitleId = bt.id
            GROUP BY bt.id, bt.title, bt.price
            ORDER BY COUNT(t) DESC
            """);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    // Monthly statistics
    @SuppressWarnings("unchecked")
    public List<Object[]> getMonthlyStats(int year) {
        Query query = entityManager.createQuery("""
            SELECT 
                MONTH(t.borrowDate),
                CAST(COUNT(t) AS int),
                CAST(COALESCE(SUM(bc.bookTitle.price), 0) AS long)
            FROM Transaction t
            JOIN BookCopy bc ON t.bookCopyId = bc.id
            WHERE YEAR(t.borrowDate) = :year
            GROUP BY MONTH(t.borrowDate)
            ORDER BY MONTH(t.borrowDate)
            """);
        query.setParameter("year", year);
        return query.getResultList();
    }
    
    // User activity by role
    @SuppressWarnings("unchecked")
    public List<UserActivityDto> getUserActivity() {
        Query query = entityManager.createQuery("""
            SELECT new com.todo.backend.dto.statistics.UserActivityDto(
                CAST(u.role AS string),
                COUNT(u)
            )
            FROM User u
            GROUP BY u.role
            ORDER BY COUNT(u) DESC
            """);
        return query.getResultList();
    }
    
    // Book condition statistics
    @SuppressWarnings("unchecked")
    public List<BookConditionDto> getBookConditionStats() {
        Query query = entityManager.createQuery("""
            SELECT new com.todo.backend.dto.statistics.BookConditionDto(
                CAST(bc.condition AS string),
                COUNT(bc)
            )
            FROM BookCopy bc
            GROUP BY bc.condition
            ORDER BY COUNT(bc) DESC
            """);
        return query.getResultList();
    }
    
    // Period-based statistics methods
    public Long getTotalTransactionsInPeriod(LocalDate startDate, LocalDate endDate) {
        Query query = entityManager.createQuery("""
            SELECT COUNT(t)
            FROM Transaction t
            WHERE t.borrowDate >= :startDate AND t.borrowDate <= :endDate
            """);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return (Long) query.getSingleResult();
    }

    public Long getTotalRevenueInPeriod(LocalDate startDate, LocalDate endDate) {
        Query query = entityManager.createQuery("""
            SELECT COALESCE(SUM(bc.bookTitle.price), 0)
            FROM Transaction t
            JOIN BookCopy bc ON t.bookCopyId = bc.id
            WHERE t.borrowDate >= :startDate AND t.borrowDate <= :endDate
            """);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        Object result = query.getSingleResult();
        return result != null ? (Long) result : 0L;
    }
    
    @SuppressWarnings("unchecked")
    public List<PopularBookDto> getPopularBooksInPeriod(int limit, LocalDate startDate, LocalDate endDate) {
        Query query = entityManager.createQuery("""
            SELECT new com.todo.backend.dto.statistics.PopularBookDto(
                bt.title,
                CAST(COUNT(t) AS int),
                CAST(COALESCE(SUM(bt.price), 0) AS long)
            )
            FROM Transaction t
            JOIN BookCopy bc ON t.bookCopyId = bc.id
            JOIN BookTitle bt ON bc.bookTitleId = bt.id
            WHERE t.borrowDate >= :startDate AND t.borrowDate <= :endDate
            GROUP BY bt.id, bt.title, bt.price
            ORDER BY COUNT(t) DESC
            """);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    // Detailed revenue calculations with clear explanations
    /**
     * Gross Revenue: Total money collected from users when they borrow books
     * This is the sum of all book prices (deposits) paid by users
     */
    public long getGrossRevenue() {
        Query query = entityManager.createQuery("""
            SELECT COALESCE(SUM(bc.bookTitle.price), 0)
            FROM Transaction t
            JOIN BookCopy bc ON t.bookCopyId = bc.id
            """);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
    
    /**
     * Net Revenue: Actual profit or loss for the library
     * Calculation: Total penalties collected - Total refunds given back to users
     * Positive = Library made profit, Negative = Library lost money
     */
    public long getNetRevenue() {
        Query query = entityManager.createQuery("""
            SELECT 
                COALESCE(SUM(td.penaltyFee), 0) - 
                COALESCE(SUM(
                    CASE 
                        WHEN t.returnedDate IS NOT NULL 
                        THEN bc.bookTitle.price - COALESCE(td.penaltyFee, 0)
                        ELSE 0 
                    END
                ), 0)
            FROM Transaction t
            JOIN BookCopy bc ON t.bookCopyId = bc.id
            LEFT JOIN TransactionDetail td ON t.id = td.transactionId
            """);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
    
    /**
     * Total Penalties: Sum of all penalty fees collected from users
     * This includes late fees, damage fees, and additional penalties
     */
    public long getTotalPenalties() {
        Query query = entityManager.createQuery("""
            SELECT COALESCE(SUM(td.penaltyFee), 0)
            FROM TransactionDetail td
            """);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
    
    /**
     * Total Refunds: Total money given back to users when returning books
     * Calculation: Sum of (book price - penalty fee) for all returned books
     */
    public long getTotalRefunds() {
        Query query = entityManager.createQuery("""
            SELECT COALESCE(SUM(
                bc.bookTitle.price - COALESCE(td.penaltyFee, 0)
            ), 0)
            FROM Transaction t
            JOIN BookCopy bc ON t.bookCopyId = bc.id
            LEFT JOIN TransactionDetail td ON t.id = td.transactionId
            WHERE t.returnedDate IS NOT NULL
            """);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
}
