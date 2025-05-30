package com.todo.backend.dao;

import com.todo.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query("SELECT a FROM Category a JOIN a.bookCategories ba WHERE ba.bookTitle.id = :bookTitleId")
    List<Category> findByBookTitleId(@RequestParam("bookTitleId") String bookTitleId);

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, String id);
}