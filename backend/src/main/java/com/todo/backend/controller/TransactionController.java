package com.todo.backend.controller;

import com.todo.backend.dto.transaction.CreateTransactionDto;
import com.todo.backend.dto.transaction.CreateTransactionFromReservationDto;
import com.todo.backend.dto.transaction.ResponseTransactionDto;
import com.todo.backend.dto.transaction.UpdateTransactionDto;
import com.todo.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        try {
            List<ResponseTransactionDto> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching transactions: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyTransactions(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<ResponseTransactionDto> transactions = transactionService.getTransactionsByUserId(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user transactions: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable String id) {
        try {
            ResponseTransactionDto transactionDto = transactionService.getTransaction(id);
            return ResponseEntity.ok(transactionDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching transaction: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody CreateTransactionDto createTransactionDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseTransactionDto createdTransaction = transactionService.createTransaction(createTransactionDto);
            return ResponseEntity.ok(createdTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating transaction: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable String id, @Valid @RequestBody UpdateTransactionDto updateTransactionDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseTransactionDto updatedTransaction = transactionService.updateTransaction(id, updateTransactionDto);
            return ResponseEntity.ok(updatedTransaction);        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating transaction: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PostMapping("/from-reservation")
    public ResponseEntity<?> createTransactionFromReservation(@Valid @RequestBody CreateTransactionFromReservationDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }            ResponseTransactionDto createdTransaction = transactionService.createTransactionFromReservation(
                dto.getReservationId(),
                dto.getBookCopyId()
            );
            return ResponseEntity.ok(createdTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating transaction from reservation: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable String id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok("Transaction deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting transaction: " + e.getMessage());
        }
    }
}
