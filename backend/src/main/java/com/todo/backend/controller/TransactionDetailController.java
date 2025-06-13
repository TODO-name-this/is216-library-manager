package com.todo.backend.controller;

import com.todo.backend.dto.transactiondetail.CreateTransactionDetailDto;
import com.todo.backend.dto.transactiondetail.UpdateTransactionDetailDto;
import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import com.todo.backend.service.TransactionDetailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/transaction-detail")
public class TransactionDetailController {
    private final TransactionDetailService transactionDetailService;

    public TransactionDetailController(TransactionDetailService transactionDetailService) {
        this.transactionDetailService = transactionDetailService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping
    public ResponseEntity<?> getAllTransactionDetails() {
        try {
            List<ResponseTransactionDetailDto> transactionDetails = transactionDetailService.getAllTransactionDetails();
            return ResponseEntity.ok(transactionDetails);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching transaction details: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionDetail(@PathVariable String transactionId) {
        try {
            ResponseTransactionDetailDto transactionDetail = transactionDetailService.getTransactionDetail(transactionId);
            return ResponseEntity.ok(transactionDetail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching transaction detail: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PostMapping
    public ResponseEntity<?> createTransactionDetail(@Valid @RequestBody CreateTransactionDetailDto createTransactionDetailDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseTransactionDetailDto createdTransactionDetail = transactionDetailService.createTransactionDetail(createTransactionDetailDto);
            return ResponseEntity.ok(createdTransactionDetail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating transaction detail: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PutMapping("/{transactionId}")
    public ResponseEntity<?> updateTransactionDetail(@PathVariable String transactionId, @Valid @RequestBody UpdateTransactionDetailDto updateTransactionDetailDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseTransactionDetailDto updatedTransactionDetail = transactionDetailService.updateTransactionDetail(transactionId, updateTransactionDetailDto);
            return ResponseEntity.ok(updatedTransactionDetail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating transaction detail: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<?> deleteTransactionDetail(@PathVariable String transactionId) {
        try {
            transactionDetailService.deleteTransactionDetail(transactionId);
            return ResponseEntity.ok("Transaction detail deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting transaction detail: " + e.getMessage());
        }
    }
}
