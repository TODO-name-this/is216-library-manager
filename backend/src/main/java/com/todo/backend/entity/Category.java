package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "CATEGORY")
@Data
public class Category {
    @Id
    @Column(name = "ID")
    @NotBlank(message = "Category ID is required")
    private String id;

    @Column(name = "NAME")
    @NotBlank(message = "Category name is required")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    // Relationship with BookCategory
    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCategory> bookCategories;
}
