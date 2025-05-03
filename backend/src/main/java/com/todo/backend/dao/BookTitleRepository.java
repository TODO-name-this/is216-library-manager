package com.todo.backend.dao;

import com.todo.backend.entity.BookTitle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BookTitleRepository extends JpaRepository<BookTitle, String> {
    Page<BookTitle> findByTitleContainingIgnoreCase(@RequestParam("title") String title, Pageable pageable);
    BookTitle findByIsbn(@RequestParam("isbn") String isbn);

    // Search by publisher
    Page<BookTitle> findByPublisherId(@RequestParam("publisherId") String publisherId, Pageable pageable);
    Page<BookTitle> findByPublisherNameContainingIgnoreCase(@RequestParam("publisherName") String publisherName, Pageable pageable);

    // Search by author
    @Query("SELECT b FROM BookTitle b JOIN b.bookAuthors bc WHERE bc.author.id = :authorId")
    Page<BookTitle> findByBookAuthors(@RequestParam("authorId") String authorId, Pageable pageable);

    @Query("SELECT b FROM BookTitle b JOIN b.bookAuthors bc WHERE bc.author.id IN :authorIds")
    Page<BookTitle> findByBookAuthorsId(List<String> authorIds, Pageable pageable);

    @Query("SELECT b FROM BookTitle b JOIN b.bookAuthors bc WHERE bc.author.name LIKE %:authorName%")
    Page<BookTitle> findByBookAuthorsName(@RequestParam("authorName") String authorName, Pageable pageable);

    @Query("SELECT b FROM BookTitle b JOIN b.bookAuthors bc WHERE bc.author.name IN :authorNames")
    Page<BookTitle> findByBookAuthorsName(List<String> authorNames, Pageable pageable);

    // Search by category
    @Query("SELECT b FROM BookTitle b JOIN b.bookCategories bc WHERE bc.category.id = :categoryId")
    Page<BookTitle> findByBookCategoryId(@RequestParam("categoryId") String categoryId, Pageable pageable);

    @Query("SELECT b FROM BookTitle b JOIN b.bookCategories bc WHERE bc.category.id IN :categoryIds")
    Page<BookTitle> findByBookCategoriesId(List<String> categoryIds, Pageable pageable);


    @Query("SELECT b FROM BookTitle b JOIN b.bookCategories bc WHERE bc.category.name LIKE %:categoryName%")
    Page<BookTitle> findByBookCategoryName(@RequestParam("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT b FROM BookTitle b JOIN b.bookCategories bc WHERE bc.category.name IN :categoryNames")
    Page<BookTitle> findByBookCategoriesName(List<String> categoryNames, Pageable pageable);
}