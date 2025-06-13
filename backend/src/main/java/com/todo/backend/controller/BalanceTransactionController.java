package com.todo.backend.controller;

import com.todo.backend.dto.balancetransaction.BalanceTransactionResponseDto;
import com.todo.backend.entity.BalanceTransactionType;
import com.todo.backend.service.BalanceTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/balance-transactions")
public class BalanceTransactionController {

    private final BalanceTransactionService balanceTransactionService;

    public BalanceTransactionController(BalanceTransactionService balanceTransactionService) {
        this.balanceTransactionService = balanceTransactionService;
    }

    /**
     * Get user's own balance transaction history
     * GET /api/balance-transactions/my
     */
    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyTransactions(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<BalanceTransactionResponseDto> transactions = balanceTransactionService.getUserTransactions(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching transactions: " + e.getMessage());
        }
    }

    /**
     * Get user's balance transaction history (admin/librarian)
     * GET /api/balance-transactions/user/{userId}
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTransactions(@PathVariable String userId) {
        try {
            List<BalanceTransactionResponseDto> transactions = balanceTransactionService.getUserTransactions(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user transactions: " + e.getMessage());
        }
    }

    /**
     * Get all balance transactions (admin only)
     * GET /api/balance-transactions
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        try {
            List<BalanceTransactionResponseDto> transactions = balanceTransactionService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching all transactions: " + e.getMessage());
        }
    }

    /**
     * Admin manually adds balance to user account
     * POST /api/balance-transactions/deposit
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PostMapping("/deposit")
    public ResponseEntity<?> addBalance(@RequestParam String userId, 
                                       @RequestParam int amount, 
                                       @RequestParam(required = false) String description) {
        try {
            if (amount <= 0) {
                return ResponseEntity.badRequest().body("Amount must be positive");
            }

            String desc = description != null ? description : "Nạp tiền tại quầy thư viện";
            BalanceTransactionResponseDto response = balanceTransactionService.adjustBalance(
                userId, BalanceTransactionType.DEPOSIT, amount, desc);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding balance: " + e.getMessage());
        }
    }

    /**
     * Admin manually withdraws balance from user account
     * POST /api/balance-transactions/withdrawal
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PostMapping("/withdrawal")
    public ResponseEntity<?> subtractBalance(@RequestParam String userId, 
                                           @RequestParam int amount, 
                                           @RequestParam(required = false) String description) {
        try {
            if (amount <= 0) {
                return ResponseEntity.badRequest().body("Amount must be positive");
            }

            String desc = description != null ? description : "Rút tiền bởi quản trị viên";
            BalanceTransactionResponseDto response = balanceTransactionService.adjustBalance(
                userId, BalanceTransactionType.WITHDRAWAL, -amount, desc);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error subtracting balance: " + e.getMessage());
        }
    }
}