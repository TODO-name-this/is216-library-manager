package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "BOOK_COPY")
@Data
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "BOOK_TITLE_ID")
    private String bookTitleId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private BookCopyStatus status;

    // escape column name to avoid SQL keyword conflict
    @Column(name = "`CONDITION`")
    @Enumerated(EnumType.STRING)
    private BookCopyCondition condition;

    // Relationship with BookTitle
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_TITLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private BookTitle bookTitle;

    // Relationship with Reservation
    @JsonIgnore
    @OneToMany(mappedBy = "bookCopy", fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    // Relationship with Transaction
    @JsonIgnore
    @OneToMany(mappedBy = "bookCopy", fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
