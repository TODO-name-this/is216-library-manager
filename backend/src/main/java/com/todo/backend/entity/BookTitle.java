package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "BOOK_TITLE")
@Data
public class BookTitle {
    @Id
    @Column(name = "ID")
    @NotBlank(message = "BookTitle ID is required")
    private String id;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "TITLE")
    @NotBlank(message = "BookTitle title is required")
    private String title;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "PUBLISHED_DATE")
    @NotBlank(message = "BookTitle published date is required")
    private String publishedDate;

    @Column(name = "PUBLISHER_ID")
    @NotBlank(message = "BookTitle publisher ID is required")
    private String publisherId;

    // Relationship with Publisher
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUBLISHER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Publisher publisher;

    // Relationship with BookAuthor
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAuthor> bookAuthors;

    // Relationship with BookCategory
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCategory> bookCategories;

    // Relationship with Review
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    // Relationship with Reservation
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    // Relationship with BookCopy
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCopy> bookCopies;
}
