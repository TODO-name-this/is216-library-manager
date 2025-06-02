package com.todo.backend.dao;

import com.todo.backend.entity.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface BookCopyRepository extends JpaRepository<BookCopy, String> {

    @Query("SELECT b FROM BookCopy bc JOIN bc.bookTitleId b WHERE bc.bookTitleId = :bookTitleId")
    Page<BookCopy> findByBookTitleId(@RequestParam("bookTitleId") String bookTitleId, Pageable pageable);

    @Query("SELECT b FROM BookCopy bc JOIN bc.bookTitleId b WHERE bc.bookTitleId = :bookTitleId AND bc.status = :status")
    Page<BookCopy> findByBookTitleIdAndStatus(@RequestParam("bookTitleId") String bookTitleId, @RequestParam("status") String status, Pageable pageable);

    BookCopy findFirstByBookTitleIdAndStatus(String bookTitleId, String status);
    
    List<BookCopy> findByBookTitleId(String bookTitleId);
}
