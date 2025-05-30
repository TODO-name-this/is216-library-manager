package com.todo.backend.service;

import com.todo.backend.dao.CategoryRepository;
import com.todo.backend.dto.category.CategoryDto;
import com.todo.backend.dto.category.ResponseCategoryDto;
import com.todo.backend.entity.Category;
import com.todo.backend.mapper.CategoryMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<ResponseCategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toResponseDto)
                .toList();
    }

    public ResponseCategoryDto getCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category ID does not exist"));

        return categoryMapper.toResponseDto(category);
    }

    public ResponseCategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);

        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        categoryRepository.save(category);

        return categoryMapper.toResponseDto(category);
    }

    public ResponseCategoryDto updateCategory(String id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category ID does not exist"));

        if (categoryRepository.existsByNameAndIdNot(categoryDto.getName(), id)) {
            throw new IllegalArgumentException("Category name already exists");
        }

        categoryMapper.updateCategoryFromDto(categoryDto, existingCategory);

        categoryRepository.save(existingCategory);

        return categoryMapper.toResponseDto(existingCategory);
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
