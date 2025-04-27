package com.todo.backend.controller;

import com.todo.backend.entity.TransactionDetail;
import com.todo.backend.service.TransactionDetailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/transactionDetail")
public class TransactionDetailController {
    private final TransactionDetailService transactionDetailService;

    public TransactionDetailController(TransactionDetailService transactionDetailService) {
        this.transactionDetailService = transactionDetailService;
    }

    @PostMapping
    public ResponseEntity<?> createTransactionDetail(@Valid @RequestBody TransactionDetail transactionDetail, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            TransactionDetail createdTransactionDetail = transactionDetailService.createTransactionDetail(transactionDetail);
            return ResponseEntity.ok(createdTransactionDetail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating transaction detail: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransactionDetail(@PathVariable String id, @Valid @RequestBody TransactionDetail transactionDetail, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            TransactionDetail updatedTransactionDetail = transactionDetailService.updateTransactionDetail(transactionDetail);
            return ResponseEntity.ok(updatedTransactionDetail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating transaction detail: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransactionDetail(@PathVariable String id) {
        try {
            transactionDetailService.deleteTransactionDetail(id);
            return ResponseEntity.ok("Transaction detail deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting transaction detail: " + e.getMessage());
        }
    }
}
