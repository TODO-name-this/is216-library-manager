package com.todo.backend.controller;

import com.todo.backend.dto.user.*;
import com.todo.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("#id == authentication.name or hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping()
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching users: " + e.getMessage());
        }
    }

    // search endpoint to get users by query string with prioritized matching
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
        @RequestParam(required = false) String q
    ) {
        try {
            return ResponseEntity.ok(userService.searchUsers(q));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error searching users: " + e.getMessage());
        }
    }

    @PreAuthorize("#id == authentication.name or hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            ResponseUserDto user = userService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user: " + e.getMessage());
        }
    }

    // Only ADMIN and LIBRARIAN can create users
    // Only ADMIN can create ADMIN or LIBRARIAN users
    @PreAuthorize(
        "(hasAuthority('ADMIN')) or " +
        "(hasAuthority('LIBRARIAN') and #createUserDto.role.name() != 'ADMIN' and #createUserDto.role.name() != 'LIBRARIAN')"
    )
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDto createUserDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseUserDto createdUser = userService.createUser(createUserDto);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating user: " + e.getMessage());
        }
    }

    // Self-update endpoint for authenticated users (limited fields)
    @PatchMapping("/self")
    public ResponseEntity<?> selfUpdateUser(@Valid @RequestBody SelfUpdateUserDto selfUpdateUserDto, BindingResult result, Authentication authentication) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
            }

            String userId = authentication.getName();
            ResponseUserDto updatedUser = userService.selfUpdateUser(userId, selfUpdateUserDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating user: " + e.getMessage());
        }
    }

    // Role-based user update endpoint for ADMIN/LIBRARIAN
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserByRole(@PathVariable String id, @Valid @RequestBody LibrarianUpdateUserDto updateUserDto, BindingResult result, Authentication authentication) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
            }

            String currentUserId = authentication.getName();
            ResponseUserDto updatedUser = userService.updateUserByRole(id, updateUserDto, currentUserId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating user: " + e.getMessage());
        }
    }

    // Only ADMIN and LIBRARIAN can delete users
    // Only ADMIN can delete ADMIN or LIBRARIAN users
    @PreAuthorize("@userService.canDeleteUser(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting user: " + e.getMessage());
        }
    }
}
