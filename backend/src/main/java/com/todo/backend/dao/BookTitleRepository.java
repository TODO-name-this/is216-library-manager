package com.todo.backend.dao;

import com.todo.backend.entity.BookTitle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

public interface BookTitleRepository extends JpaRepository<BookTitle, String> {
    Page<BookTitle> findByTitleContainingIgnoreCase(@RequestParam("title") String title, Pageable pageable);
    BookTitle findByIsbn(@RequestParam("isbn") String isbn);

    // Search by publisher
    Page<BookTitle> findByPublisherId(@RequestParam("publisherId") String publisherId, Pageable pageable);

    // Search by author
    @Query("SELECT b FROM BookTitle b JOIN b.bookAuthors bc WHERE bc.author.id = :authorId")
    Page<BookTitle> findByBookAuthors(@RequestParam("authorId") String authorId, Pageable pageable);

    // Search by category
    @Query("SELECT b FROM BookTitle b JOIN b.bookCategories bc WHERE bc.category.id = :categoryId")
    Page<BookTitle> findByBookCategories(@RequestParam("categoryId") String categoryId, Pageable pageable);
}