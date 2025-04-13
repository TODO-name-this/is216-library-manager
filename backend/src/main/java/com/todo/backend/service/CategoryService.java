package com.todo.backend.service;

import com.todo.backend.dao.BookRepository;
import com.todo.backend.dao.CategoryRepository;
import com.todo.backend.entity.Book;
import com.todo.backend.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {
        if(categoryRepository.existsById(category.getId())) {
            throw new IllegalArgumentException("Category with ID already exists: " + category.getId());
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(String id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category with ID does not exist: " + id));

        if (category.getName() != null) {
            existingCategory.setName(category.getName());
        }
        if (category.getDescription() != null) {
            existingCategory.setDescription(category.getDescription());
        }

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(String Id) {
        Category category = categoryRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("Category with ID does not exist: " + Id));
        categoryRepository.delete(category);
    }
}
