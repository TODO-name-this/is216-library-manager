package com.todo.backend.controller;

import com.todo.backend.dto.transaction.CreateTransactionDto;
import com.todo.backend.dto.transaction.ResponseTransactionDto;
import com.todo.backend.dto.transaction.UpdateTransactionDto;
import com.todo.backend.entity.Transaction;
import com.todo.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable String id, @Valid @RequestBody UpdateTransactionDto updateTransactionDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseTransactionDto updatedTransaction = transactionService.updateTransaction(id, updateTransactionDto);
            return ResponseEntity.ok(updatedTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating transaction: " + e.getMessage());
        }
    }

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
