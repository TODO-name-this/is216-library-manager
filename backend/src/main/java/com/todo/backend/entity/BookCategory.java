package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todo.backend.entity.compositekey.BookCategoryPrimaryKey;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "BOOK_CATEGORY")
@Data
@IdClass(BookCategoryPrimaryKey.class)
public class BookCategory {
    @Id
    @Column(name = "BOOK_TITLE_ID")
    private String bookTitleId;

    @Id
    @Column(name = "CATEGORY_ID")
    private String categoryId;

    // Relationship with Category
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Category category;

    // Relationship with BookTitle
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_TITLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private BookTitle bookTitle;
}
