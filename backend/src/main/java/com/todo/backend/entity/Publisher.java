package com.todo.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "PUBLISHER")
@Data
public class Publisher {
    @Id
    @Column(name = "ID")
    @NotNull
    private String id;

    @Column(name = "LOGO_URL")
    private String logoUrl;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    // Relationship with Book
    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books;
}
