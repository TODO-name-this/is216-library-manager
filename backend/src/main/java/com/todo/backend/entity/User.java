package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.todo.backend.entity.identity.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "USER")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    @NotBlank(message = "User ID is required")
    private String id;

    @NotBlank(message = "CCCD is required")
    @Column(name = "CCCD", unique = true)
    private String cccd;

    @Column(name = "AVATAR_URL")
    private String avatarUrl;

    @NotBlank(message = "Name is required")
    @Column(name = "NAME")
    private String name;

    @NotBlank(message = "Birthday is required")
    @Column(name = "DOB")
    private String dob;

    @NotBlank(message = "Email is required")
    @Column(name = "EMAIL", unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // only allow deserialization
    @Column(name = "PASSWORD")
    private String password;

    @NotBlank(message = "Role is required")
    @Column(name = "ROLE")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    // Relationship with Review
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews;

    // Relationship with Reservation
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Reservation> reservations;

    // Relationship with Transaction
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Transaction> transactions;
}
