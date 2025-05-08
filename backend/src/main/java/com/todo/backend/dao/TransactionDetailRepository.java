package com.todo.backend.dao;

import com.todo.backend.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, String> {
    @Query("SELECT td FROM TransactionDetail td JOIN Transaction tr ON td.transactionId = tr.id WHERE tr.userId = :userId AND td.returnedDate IS NULL")
    List<TransactionDetail> findByUserIdAndNotReturned(String userId);

    List<TransactionDetail> findByTransactionId(String transactionId);
    TransactionDetail findByTransactionIdAndBookCopyId(String transactionId, String bookCopyId);
}
