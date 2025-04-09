package com.todo.backend.dao;

import com.todo.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

public interface ReviewRepository extends JpaRepository<Review, String> {

    Page<Review> findByBookId(@RequestParam("bookId") String bookId, Pageable pageable);

    Review findByUserIdAndBookId(String userId, String bookId);

    @Modifying
    @Query("DELETE FROM Review WHERE bookId IN :BOOK_ID")
    void deleteByBookId(@RequestParam("BOOK_ID") String bookId);
}
