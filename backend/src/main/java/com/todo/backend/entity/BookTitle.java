package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "BOOK_TITLE")
@Data
public class BookTitle {
    @Id
    @Column(name = "ID")
    @NotNull
    private String id;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "PUBLISHED_DATE")
    private String publishedDate;

    @Column(name = "PUBLISHER_ID")
    private String publisherId;

    // Relationship with Publisher
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUBLISHER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Publisher publisher;

    // Relationship with BookAuthor
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY)
    private List<BookAuthor> bookAuthors;

    // Relationship with BookCategory
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY)
    private List<BookCategory> bookCategories;

    // Relationship with Review
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY)
    private List<Review> reviews;

    // Relationship with Reservation
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    // Relationship with BookCopy
    @JsonIgnore
    @OneToMany(mappedBy = "bookTitle", fetch = FetchType.LAZY)
    private List<BookCopy> bookCopies;
}
