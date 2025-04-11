package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "BOOK")
@Data
public class Book {
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

    @Column(name = "RATING_COUNT")
    private int ratingCount;

    @Column(name = "RATING")
    private double rating;

    @Column(name = "PUBLISHED_DATE")
    private String publishedDate;

    @Column(name = "PUBLISHER_ID")
    private String publisherId;

    // Relationship with Review
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    // Relationship with Publisher
    @ManyToOne
    @JoinColumn(name = "PUBLISHER_ID", insertable = false, updatable = false)
    private Publisher publisher;

    // Relationship with Transaction
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    // Relationship with Reservation
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    // Relationship with BookAuthor
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookAuthor> bookAuthors;

    // Relationship with BookCategory
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookCategory> bookCategories;

    // Relationship with Inventory
    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Inventory inventory;
}
