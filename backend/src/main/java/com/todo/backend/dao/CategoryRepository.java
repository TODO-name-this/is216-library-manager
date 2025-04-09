package com.todo.backend.dao;

import com.todo.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query("SELECT a FROM Category a JOIN a.bookCategories ba WHERE ba.book.id = :bookId")
    List<Category> findByBookId(@RequestParam("bookId") String bookId);
}