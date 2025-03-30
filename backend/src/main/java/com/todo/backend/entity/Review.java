package com.todo.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "REVIEW")
@Data
public class Review {
    @Id
    @Column(name = "ID")
    @NotNull
    private String id;

    @Column(name = "DATE")
    private String date;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "COMMENT")
    private String comment;

    @Column(name = "SCORE")
    private int score;

    @Column(name = "BOOK_ID")
    private String bookId;

    @Column(name = "USER_ID")
    private String userId;

    // Relationships with Book and User
    @ManyToOne
    @JoinColumn(name = "BOOK_ID", insertable = false, updatable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private User user;
}
