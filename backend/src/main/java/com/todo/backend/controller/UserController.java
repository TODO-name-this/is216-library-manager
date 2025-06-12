package com.todo.backend.controller;

import com.todo.backend.dto.user.PartialUpdateUserDto;
import com.todo.backend.dto.user.ResponseUserDto;
import com.todo.backend.dto.user.CreateUserDto;
import com.todo.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("#id == authentication.name or hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping()
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching users: " + e.getMessage());
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

    // Only owner of the account or ADMIN/LIBRARIAN can update user details
    // Only ADMIN can update ADMIN or LIBRARIAN users
    @PreAuthorize(
        "#id == authentication.name or " +
        "hasAnyAuthority('ADMIN') or  " +
        "(hasAnyAuthority('LIBRARIAN') and #partialUpdateUserDto.role.name() != 'LIBRARIAN' and #partialUpdateUserDto.role.name() != 'ADMIN')"
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody PartialUpdateUserDto partialUpdateUserDto) {
        try {
            ResponseUserDto updatedUser = userService.updateUser(id, partialUpdateUserDto);
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
