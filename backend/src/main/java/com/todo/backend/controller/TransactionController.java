package com.todo.backend.controller;

import com.todo.backend.dto.transaction.TransactionCreateDto;
import com.todo.backend.dto.transaction.TransactionUpdateDto;
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

    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionCreateDto transactionCreateDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            Transaction createdTransaction = transactionService.createTransaction(transactionCreateDto);
            return ResponseEntity.ok(createdTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating transaction: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable String id, @Valid @RequestBody TransactionUpdateDto transactionUpdateDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            Transaction updatedTransaction = transactionService.updateTransaction(transactionUpdateDto);
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
