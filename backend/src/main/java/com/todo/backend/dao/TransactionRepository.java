package com.todo.backend.dao;

import com.todo.backend.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Page<Transaction> findByUserId(@RequestParam("user_id") String userId, Pageable pageable);

    Page<Transaction> findByBookId(@RequestParam("book_id") String bookId, Pageable pageable);
}
