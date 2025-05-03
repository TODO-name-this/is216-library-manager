package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "RESERVATION")
@Data
public class Reservation {
    @Id
    @Column(name = "ID")
    @NotBlank(message = "Reservation ID is required")
    private String id;

    @Column(name = "RESERVATION_DATE")
    @NotBlank(message = "Reservation date is required")
    private String reservationDate;

    @Column(name = "EXPIRATION_DATE")
    @NotBlank(message = "Expiration date is required")
    private String expirationDate;

    @Column(name = "STATUS")
    @NotBlank(message = "Status is required")
    private String status;

    @Column(name = "BOOK_TITLE_ID")
    @NotBlank(message = "[Reservation] Book title ID is required")
    private String bookTitleId;

    @Column(name= "BOOK_COPY_ID")
    private String bookCopyId;

    @Column(name = "USER_ID")
    @NotBlank(message = "[Reservation] User ID is required")
    private String userId;

    // Relationship with BookTitle
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_TITLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private BookTitle bookTitle;

    // Relationship with BookCopy
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_COPY_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private BookCopy bookCopy;

    // Relationship with User
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private User user;
}
