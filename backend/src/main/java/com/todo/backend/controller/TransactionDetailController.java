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

@CrossOrigin("http://localhost:3000")
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
    @GetMapping("/{transactionId}/{bookCopyId}")
    public ResponseEntity<?> getTransactionDetail(@PathVariable String transactionId, @PathVariable String bookCopyId) {
        try {
            ResponseTransactionDetailDto transactionDetail = transactionDetailService.getTransactionDetail(transactionId, bookCopyId);
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
    @PutMapping("/{transactionId}/{bookCopyId}")
    public ResponseEntity<?> updateTransactionDetail(@PathVariable String transactionId, @PathVariable String bookCopyId, @Valid @RequestBody UpdateTransactionDetailDto updateTransactionDetailDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseTransactionDetailDto updatedTransactionDetail = transactionDetailService.updateTransactionDetail(transactionId, bookCopyId, updateTransactionDetailDto);
            return ResponseEntity.ok(updatedTransactionDetail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating transaction detail: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/{transactionId}/{bookCopyId}")
    public ResponseEntity<?> deleteTransactionDetail(@PathVariable String transactionId, @PathVariable String bookCopyId) {
        try {
            transactionDetailService.deleteTransactionDetail(transactionId, bookCopyId);
            return ResponseEntity.ok("Transaction detail deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting transaction detail: " + e.getMessage());
        }
    }
}
