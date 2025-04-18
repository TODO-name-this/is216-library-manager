package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // Relationship with BookTitle
    @JsonIgnore
    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BookTitle> bookTitles;
}
