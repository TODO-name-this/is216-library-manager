package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "AUTHOR")
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "AVATAR_URL")
    private String avatarUrl;

    @Column(name = "NAME")
    private String name;

    @Column(name = "BIRTHDAY")
    private LocalDate birthday;

    @Column(name = "BIOGRAPHY")
    private String biography;

    // Relationship with BookAuthor
    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAuthor> bookAuthors;
}