package com.todo.backend.dao;

import com.todo.backend.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

public interface BookRepository extends JpaRepository<Book, String> {

    Page<Book> findByTitleContainingIgnoreCase(@RequestParam("title") String title, Pageable pageable);

    Page<Book> findByPublisherId(@RequestParam("publisher_id") String publisherId, Pageable pageable);

    Page<Book> findByBookCategories(@RequestParam("category_id") String categoryId, Pageable pageable);

    Page<Book> findByIsbn(String isbn, Pageable pageable);
}