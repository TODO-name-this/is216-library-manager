package com.todo.backend.controller;

import com.todo.backend.entity.Category;
import com.todo.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api.category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/secure/add")
    public ResponseEntity<Category> addCategory(@AuthenticationPrincipal Jwt jwt,
                                                @Valid @RequestBody Category category) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Category savedCategory = categoryService.createCategory(category);
            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/secure/update/{categoryId}")
    public ResponseEntity<Category> updateCategory(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable String categoryId,
                                                   @RequestBody Category updatedCategory) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Category updated = categoryService.updateCategory(categoryId, updatedCategory);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/secure/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable String categoryId) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isAdmin(Jwt jwt) {
        String role = jwt.getClaimAsString("userType");
        return "admin".equalsIgnoreCase(role);
    }
}
