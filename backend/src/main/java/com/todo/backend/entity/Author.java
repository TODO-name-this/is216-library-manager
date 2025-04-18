package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "AUTHOR")
@Data
public class Author {
    @Id
    @Column(name = "ID")
    @NotNull
    private String id;

    @Column(name = "AVATAR_URL")
    private String avatarUrl;

    @Column(name = "NAME")
    private String name;

    @Column(name = "BIRTHDAY")
    private String birthday;

    @Column(name = "BIOGRAPHY")
    private String biography;

    // Relationship with BookAuthor
    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<BookAuthor> bookAuthors;
}