package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "BOOK_TITLE_ID")
    @NotNull
    private String bookTitleId;

    @Id
    @Column(name = "AUTHOR_ID")
    @NotNull
    private String authorId;

    // Relationship with Author
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Author author;

    // Relationship with BookTitle
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_TITLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private BookTitle bookTitle;
}
