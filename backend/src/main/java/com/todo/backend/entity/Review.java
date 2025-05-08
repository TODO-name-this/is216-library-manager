package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "REVIEW")
@Data
public class Review {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "DATE")
    private LocalDate date;

    @Column(name = "COMMENT")
    private String comment;

    @Column(name = "STAR")
    private int star;

    @Column(name = "BOOK_TITLE_ID")
    private String bookTitleId;

    @Column(name = "USER_ID")
    private String userId;

    // Relationship with BookTitle
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_TITLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private BookTitle bookTitle;

    // Relationship with User
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private User user;
}
