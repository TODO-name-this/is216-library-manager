package com.todo.backend.entity;

import com.todo.backend.entity.compositekey.BookAuthorPrimaryKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "BOOK_AUTHOR")
@Data
@IdClass(BookAuthorPrimaryKey.class)
public class BookAuthor {
    @Id
    @Column(name = "BOOK_ID")
    @NotNull
    private String bookId;

    @Id
    @Column(name = "AUTHOR_ID")
    @NotNull
    private String authorId;

    // Relationships with Book and Author
    @ManyToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Author author;
}
