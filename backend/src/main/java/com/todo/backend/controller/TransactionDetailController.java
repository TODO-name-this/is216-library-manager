package com.todo.backend.controller;

import com.todo.backend.service.TransactionDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/transactionDetail")
public class TransactionDetailController {
    private final TransactionDetailService transactionDetailService;

    public TransactionDetailController(TransactionDetailService transactionDetailService) {
        this.transactionDetailService = transactionDetailService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> createTransactionDetail() {
        return ResponseEntity.badRequest().body("Not implemented yet");
    }
}
