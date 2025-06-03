package com.todo.backend.dao;

import com.todo.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByUserId(@RequestParam("userId") String userId);
    List<Transaction> findByUserIdAndReturnedDateIsNull(@RequestParam("userId") String userId);
    List<Transaction> findByBookCopyIdAndReturnedDateIsNull(String bookCopyId);
//    Page<Transaction> findByUserId(@RequestParam("user_id") String userId, Pageable pageable);
//
//    Page<Transaction> findByBookId(@RequestParam("book_id") String bookId, Pageable pageable);
}
