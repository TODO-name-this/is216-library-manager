package com.todo.backend.service;

import com.todo.backend.dao.CategoryRepository;
import com.todo.backend.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {
        if (categoryRepository.existsById(category.getId())) {
            throw new RuntimeException("Category ID already exists");
        }

        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category) {
        if (!categoryRepository.existsById(category.getId())) {
            throw new RuntimeException("Category ID does not exist");
        }

        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category ID does not exist"));

        if (existingCategory.getBookCategories() != null && !existingCategory.getBookCategories().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with associated books");
        }

        categoryRepository.delete(existingCategory);
    }
}
