package com.todo.backend.dao;

import com.todo.backend.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

public interface AuthorRepository extends JpaRepository<Author, String> {

    Page<Author> findByNameContainingIgnoreCase(@RequestParam("name") String name, Pageable pageable);
}