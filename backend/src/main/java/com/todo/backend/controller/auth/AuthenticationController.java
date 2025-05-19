package com.todo.backend.controller.auth;

import com.todo.backend.controller.auth.dto.*;
import com.todo.backend.entity.identity.UserRole;
import com.todo.backend.utils.auth.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authManager;

    @GetMapping("/test")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("test");
    }

    // login route
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto req, BindingResult bindingResult) {
        Authentication auth;
        try {
            auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.cccd(), req.password())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        SecurityContextHolder.getContext().setAuthentication(auth);
        var userDetails = (UserDetails) auth.getPrincipal();
        // Optionally get authorities from userDetails if not embedded in the token
        var firstAuthority = userDetails.getAuthorities().stream().findFirst().orElseThrow().getAuthority();
        var role = UserRole.valueOf(firstAuthority);

        // username is the id, because fuck java
        var refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());
        var accessToken = jwtUtils.generateAccessToken(userDetails.getUsername(), role);

        return ResponseEntity.ok(new LoginResultDto(
                accessToken, refreshToken
        ));
    }
}
