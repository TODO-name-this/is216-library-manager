package com.todo.backend.entity;

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

    @Column(name = "BOOK_ID")
    private String bookId;

    @Column(name = "USER_ID")
    private String userId;

    // Relationships with Book and User
    @ManyToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private User user;
}
