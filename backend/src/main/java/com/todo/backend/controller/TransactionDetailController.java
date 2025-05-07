package com.todo.backend.controller;

import com.todo.backend.entity.TransactionDetail;
import com.todo.backend.service.TransactionDetailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/transaction/{id}/detail")
public class TransactionDetailController {
    private final TransactionDetailService transactionDetailService;

    public TransactionDetailController(TransactionDetailService transactionDetailService) {
        this.transactionDetailService = transactionDetailService;
    }
}
