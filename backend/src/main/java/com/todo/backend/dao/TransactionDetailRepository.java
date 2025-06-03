package com.todo.backend.dao;

import com.todo.backend.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(exported = false)
public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, String> {
    TransactionDetail findByTransactionId(String transactionId);
}
