package com.todo.backend.dao;

import com.todo.backend.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PublisherRepository extends JpaRepository<Publisher, String> {
    List<Publisher> findByNameContainingIgnoreCase(@RequestParam("name") String name);

    Publisher findByBookTitlesId(@RequestParam("bookTitleId") String bookTitleId);
}
