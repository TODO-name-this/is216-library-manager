package com.todo.backend.dao;

import com.todo.backend.entity.BalanceTransaction;
import com.todo.backend.entity.BalanceTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BalanceTransactionRepository extends JpaRepository<BalanceTransaction, String> {
    
    /**
     * Find all balance transactions for a specific user, ordered by timestamp (newest first)
     */
    List<BalanceTransaction> findByUserIdOrderByTimestampDesc(String userId);
    
    /**
     * Find all balance transactions ordered by timestamp (newest first)
     */
    List<BalanceTransaction> findAllByOrderByTimestampDesc();
    
    /**
     * Find balance transactions by type for a specific user
     */
    List<BalanceTransaction> findByUserIdAndTypeOrderByTimestampDesc(String userId, BalanceTransactionType type);
    
    /**
     * Find balance transactions within a date range
     */
    @Query("SELECT bt FROM BalanceTransaction bt WHERE bt.timestamp BETWEEN :startDate AND :endDate ORDER BY bt.timestamp DESC")
    List<BalanceTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find balance transactions for a user within a date range
     */
    @Query("SELECT bt FROM BalanceTransaction bt WHERE bt.userId = :userId AND bt.timestamp BETWEEN :startDate AND :endDate ORDER BY bt.timestamp DESC")
    List<BalanceTransaction> findByUserIdAndDateRange(@Param("userId") String userId, 
                                                    @Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
}
