package com.todo.backend.dao;

import com.todo.backend.dto.bookcopy.BookCopyWithDueInfoDto;
import com.todo.backend.entity.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface BookCopyRepository extends JpaRepository<BookCopy, String> {

    @Query("SELECT b FROM BookCopy bc JOIN bc.bookTitleId b WHERE bc.bookTitleId = :bookTitleId")
    Page<BookCopy> findByBookTitleId(@RequestParam("bookTitleId") String bookTitleId, Pageable pageable);

    @Query("SELECT b FROM BookCopy bc JOIN bc.bookTitleId b WHERE bc.bookTitleId = :bookTitleId AND bc.status = :status")
    Page<BookCopy> findByBookTitleIdAndStatus(@RequestParam("bookTitleId") String bookTitleId, @RequestParam("status") String status, Pageable pageable);

    BookCopy findFirstByBookTitleIdAndStatus(String bookTitleId, String status);
    
    List<BookCopy> findByBookTitleId(String bookTitleId);

    @Query("""
    SELECT new com.todo.backend.dto.bookcopy.BookCopyWithDueInfoDto(
           bc.id,
           bc.status,
           bc.condition,
           bt.title,
           bt.id,
           bt.price,
           t.dueDate,
           t.borrowDate,
           t.userId,
           u.name,
           u.cccd,
           CASE WHEN t.dueDate IS NOT NULL AND t.dueDate < CURRENT_DATE AND t.returnedDate IS NULL
                THEN true
                ELSE false
           END
    )
    FROM BookCopy bc
    LEFT JOIN BookTitle bt ON bc.bookTitleId = bt.id
    LEFT JOIN Transaction t ON bc.id = t.bookCopyId
        AND t.returnedDate IS NULL
        AND t.id = (
            SELECT t2.id
            FROM Transaction t2
            WHERE t2.bookCopyId = bc.id
                AND t2.returnedDate IS NULL
            ORDER BY t2.borrowDate DESC
            LIMIT 1
        )
    LEFT JOIN User u ON t.userId = u.id
    ORDER BY bc.id
    """)
    List<BookCopyWithDueInfoDto> findAllBookCopiesWithDueInfo();

    @Query("""
    SELECT new com.todo.backend.dto.bookcopy.BookCopyWithDueInfoDto(
           bc.id,
           bc.status,
           bc.condition,
           bt.title,
           bt.id,
           bt.price,
           t.dueDate,
           t.borrowDate,
           t.userId,
           u.name,
           u.cccd,
           CASE WHEN t.dueDate IS NOT NULL AND t.dueDate < CURRENT_DATE AND t.returnedDate IS NULL
                THEN true
                ELSE false
           END
    )
    FROM BookCopy bc
    LEFT JOIN BookTitle bt ON bc.bookTitleId = bt.id
    LEFT JOIN Transaction t ON bc.id = t.bookCopyId
        AND t.returnedDate IS NULL
        AND t.id = (
            SELECT t2.id
            FROM Transaction t2
            WHERE t2.bookCopyId = bc.id
                AND t2.returnedDate IS NULL
            ORDER BY t2.borrowDate DESC
            LIMIT 1
        )
    LEFT JOIN User u ON t.userId = u.id
    WHERE bc.id = :bookCopyId
    """)
    Optional<BookCopyWithDueInfoDto> findBookCopyWithDueInfo(@Param("bookCopyId") String bookCopyId);

    @Query("""
    SELECT new com.todo.backend.dto.bookcopy.BookCopyWithDueInfoDto(
           bc.id,
           bc.status,
           bc.condition,
           bt.title,
           bt.id,
           bt.price,
           t.dueDate,
           t.borrowDate,
           t.userId,
           u.name,
           u.cccd,
           true
    )
    FROM BookCopy bc
    JOIN BookTitle bt ON bc.bookTitleId = bt.id
    JOIN Transaction t ON bc.id = t.bookCopyId
        AND t.returnedDate IS NULL
        AND t.dueDate < CURRENT_DATE
        AND t.id = (
            SELECT t2.id
            FROM Transaction t2
            WHERE t2.bookCopyId = bc.id
                AND t2.returnedDate IS NULL
            ORDER BY t2.borrowDate DESC
            LIMIT 1
        )
    JOIN User u ON t.userId = u.id
    ORDER BY t.dueDate ASC
    """)
    List<BookCopyWithDueInfoDto> findOverdueBookCopies();
}
