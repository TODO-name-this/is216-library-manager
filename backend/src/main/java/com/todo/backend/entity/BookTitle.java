package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "BOOK_TITLE")
@Data
public class BookTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "CAN_BORROW")
    private boolean canBorrow;

    @Column(name = "PRICE")
    private int price;

    @Column(name = "PUBLISHED_DATE")
    private LocalDate publishedDate;

    @Column(name = "PUBLISHER_ID")
    private String publisherId;

    // Inventory Management Fields
    @Column(name = "TOTAL_COPIES")
    private int totalCopies;

    @Column(name = "MAX_ONLINE_RESERVATIONS")
    private int maxOnlineReservations;

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
