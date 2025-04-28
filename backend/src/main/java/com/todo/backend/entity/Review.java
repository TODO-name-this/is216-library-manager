package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "REVIEW")
@Data
public class Review {
    @Id
    @Column(name = "ID")
    @NotBlank(message = "Review ID is required")
    private String id;

    @Column(name = "DATE")
    @NotBlank(message = "Review date is required")
    private String date;

    @Column(name = "COMMENT")
    private String comment;

    @Column(name = "STAR")
    @NotNull(message = "Review star rating is required")
    private int star;

    @Column(name = "BOOK_TITLE_ID")
    @NotBlank(message = "[Review] Book title ID is required")
    private String bookTitleId;

    @Column(name = "USER_ID")
    @NotBlank(message = "[Review] User ID is required")
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
