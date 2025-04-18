package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "RESERVATION")
@Data
public class Reservation {
    @Id
    @Column(name = "ID")
    @NotNull
    private String id;

    @Column(name = "RESERVATION_DATE")
    private String reservationDate;

    @Column(name = "EXPIRATION_DATE")
    private String expirationDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "QUANTITY")
    private int quantity;

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
