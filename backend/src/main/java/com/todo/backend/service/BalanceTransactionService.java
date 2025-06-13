package com.todo.backend.service;

import com.todo.backend.dao.BalanceTransactionRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.balancetransaction.BalanceTransactionResponseDto;
import com.todo.backend.entity.BalanceTransaction;
import com.todo.backend.entity.BalanceTransactionType;
import com.todo.backend.entity.BalanceTransactionStatus;
import com.todo.backend.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BalanceTransactionService {

    private final BalanceTransactionRepository balanceTransactionRepository;
    private final UserRepository userRepository;

    public BalanceTransactionService(BalanceTransactionRepository balanceTransactionRepository, 
                                   UserRepository userRepository) {
        this.balanceTransactionRepository = balanceTransactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Log a balance transaction (used by other services)
     */
    public void logTransaction(String userId, BalanceTransactionType type, int amount, 
                             String description, int balanceAfter) {
        BalanceTransaction transaction = new BalanceTransaction(
                UUID.randomUUID().toString(),
                userId,
                type,
                amount,
                description,
                balanceAfter
        );
        
        balanceTransactionRepository.save(transaction);
    }

    /**
     * Get all balance transactions for a user
     */
    public List<BalanceTransactionResponseDto> getUserTransactions(String userId) {
        List<BalanceTransaction> transactions = balanceTransactionRepository.findByUserIdOrderByTimestampDesc(userId);
        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all balance transactions (admin only)
     */
    public List<BalanceTransactionResponseDto> getAllTransactions() {
        List<BalanceTransaction> transactions = balanceTransactionRepository.findAllByOrderByTimestampDesc();
        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Admin manually adjusts user balance
     */
    public BalanceTransactionResponseDto adjustBalance(String userId, BalanceTransactionType type, 
                                                     int amount, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        int newBalance = user.getBalance() + amount;
        user.setBalance(newBalance);
        userRepository.save(user);

        // Log the transaction
        BalanceTransaction transaction = new BalanceTransaction(
                UUID.randomUUID().toString(),
                userId,
                type,
                amount,
                description,
                newBalance
        );
        
        balanceTransactionRepository.save(transaction);
        return convertToDto(transaction);
    }

    /**
     * Convert entity to DTO
     */
    private BalanceTransactionResponseDto convertToDto(BalanceTransaction transaction) {
        return new BalanceTransactionResponseDto(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                transaction.getBalanceAfter(),
                transaction.getStatus()
        );
    }
}