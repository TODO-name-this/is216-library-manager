package com.todo.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "CATEGORY")
@Data
public class Category {
    @Id
    @Column(name = "ID")
    @NotNull
    private String id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    // Relationship with BookCategory
     @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     private List<BookCategory> bookCategories;
}
