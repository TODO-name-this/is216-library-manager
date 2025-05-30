package com.todo.backend.dao;

import com.todo.backend.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface PublisherRepository extends JpaRepository<Publisher, String> {
    List<Publisher> findByNameContainingIgnoreCase(@RequestParam("name") String name);

    @Query("SELECT p FROM Publisher p JOIN p.bookTitles bt WHERE bt.id = :bookTitleId")
    Publisher findByBookTitleId(@RequestParam("bookTitleId") String bookTitleId);
}
