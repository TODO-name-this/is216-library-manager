package com.todo.backend.service;

import com.todo.backend.dao.TransactionRepository;
import com.todo.backend.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TransactionService {
    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        if (transactionRepository.existsById(transaction.getId())) {
            throw new IllegalArgumentException("Transaction already exists!");
        }
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(String id, Transaction updated) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction with ID does not exist: " + id));

        if (updated.getBorrowDate() != null)
            existing.setBorrowDate(updated.getBorrowDate());

        if (updated.getDueDate() != null)
            existing.setDueDate(updated.getDueDate());

        if (updated.getReturnDate() != null)
            existing.setReturnDate(updated.getReturnDate());

        if (updated.getAmount() != 0)
            existing.setAmount(updated.getAmount());

        return transactionRepository.save(existing);
    }

    public void deleteTransaction(String id) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction with ID does not exist: " + id));
        transactionRepository.delete(existing);
    }
}
