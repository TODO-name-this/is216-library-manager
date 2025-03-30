package com.todo.backend.entity;

import com.todo.backend.entity.compositekey.BookCategoryPrimaryKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "BOOK_CATEGORY")
@Data
@IdClass(BookCategoryPrimaryKey.class)
public class BookCategory {
    @Id
    @Column(name = "BOOK_ID")
    @NotNull
    private String bookId;

    @Id
    @Column(name = "CATEGORY_ID")
    @NotNull
    private String categoryId;

    // Relationships with Book and Category
    @ManyToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Category category;
}
