package com.todo.backend.service;

import com.todo.backend.dao.TransactionDetailRepository;
import com.todo.backend.entity.TransactionDetail;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TransactionDetailService {
    private final TransactionDetailRepository transactionDetailRepository;

    public TransactionDetailService(TransactionDetailRepository transactionDetailRepository) {
        this.transactionDetailRepository = transactionDetailRepository;
    }

    public TransactionDetail createTransactionDetail(TransactionDetail transactionDetail) {
        return transactionDetailRepository.save(transactionDetail);
    }

    public TransactionDetail updateTransactionDetail(TransactionDetail transactionDetail) {
        return transactionDetailRepository.save(transactionDetail);
    }

    public void deleteTransactionDetail(TransactionDetail transactionDetail) {
        transactionDetailRepository.delete(transactionDetail);
    }
}
