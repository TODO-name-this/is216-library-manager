package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "AUTHOR")
@Data
public class Author {
    @Id
    @Column(name = "ID")
    @NotBlank(message = "Author ID is required")
    private String id;

    @Column(name = "AVATAR_URL")
    private String avatarUrl;

    @Column(name = "NAME")
    @NotBlank(message = "Author name is required")
    private String name;

    @Column(name = "BIRTHDAY")
    private String birthday;

    @Column(name = "BIOGRAPHY")
    private String biography;

    // Relationship with BookAuthor
    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAuthor> bookAuthors;
}