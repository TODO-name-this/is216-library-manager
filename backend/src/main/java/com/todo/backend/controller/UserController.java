package com.todo.backend.controller;

import com.todo.backend.dto.user.ResponseUserDto;
import com.todo.backend.dto.user.UserDto;
import com.todo.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
        "hasAnyAuthority('ADMIN', 'LIBRARIAN') and " +
        "(#userDto.role != 'ADMIN' and #userDto.role != 'LIBRARIAN' or hasAuthority('ADMIN'))"
    )
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseUserDto createdUser = userService.createUser(userDto);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating user: " + e.getMessage());
        }
    }

    @PreAuthorize("#id == authentication.name or hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseUserDto updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating user: " + e.getMessage());
        }
    }

    // Only ADMIN and LIBRARIAN can delete users
    // Only ADMIN can delete ADMIN or LIBRARIAN users
    @PreAuthorize(
        "hasAnyAuthority('ADMIN','LIBRARIAN') and " +
        "((#target = @userService.getUser(#id)).role != 'ADMIN' and #target.role != 'LIBRARIAN' or hasAuthority('ADMIN'))"
    )
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
