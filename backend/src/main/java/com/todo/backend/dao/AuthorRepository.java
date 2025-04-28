package com.todo.backend.dao;

import com.todo.backend.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, String> {
    Page<Author> findByNameContainingIgnoreCase(@RequestParam("name") String name, Pageable pageable);

    @Query("SELECT a FROM Author a JOIN a.bookAuthors ba WHERE ba.bookTitle.id = :bookTitleId")
    List<Author> findByBookTitleId(@Param("bookTitleId") String bookTitleId);
}