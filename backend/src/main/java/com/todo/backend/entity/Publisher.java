package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "PUBLISHER")
@Data
public class Publisher {
    @Id
    @Column(name = "ID")
    @NotBlank(message = "Publisher ID is required")
    private String id;

    @Column(name = "LOGO_URL")
    private String logoUrl;

    @Column(name = "NAME")
    @NotBlank(message = "Publisher name is required")
    private String name;

    @Column(name = "ADDRESS")
    @NotBlank(message = "Publisher address is required")
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
