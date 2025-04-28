package com.todo.backend.dao;

import com.todo.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

public interface ReviewRepository extends JpaRepository<Review, String> {
    Page<Review> findByBookTitleId(@RequestParam("bookTitleId") String bookTitleId, Pageable pageable);

    Review findByUserIdAndBookTitleId(String userId, String bookTitleId);

    Page<Review> findByUserId(@RequestParam("userId") String userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.bookTitle.id = :bookTitleId")
    void deleteByBookTitleId(@Param("bookTitleId") String bookTitleId);
}
