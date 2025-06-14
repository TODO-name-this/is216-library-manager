package com.todo.backend.dao;

import com.todo.backend.entity.User;
import com.todo.backend.entity.identity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByRole(@RequestParam("role") UserRole role, Pageable pageable);
    Optional<User> findByCccd(@RequestParam("cccd") String cccd);
    Page<User> findByNameContainingIgnoreCase(@RequestParam("name") String name, Pageable pageable);

    // Simple test methods
    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findByCccdContainingIgnoreCase(String cccd);
    List<User> findByEmailContainingIgnoreCase(String email);

    // Exact match methods
    List<User> findByNameIgnoreCase(String name);
    List<User> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, String id);
    boolean existsByCccd(String cccd);
    boolean existsByCccdAndIdNot(String cccd, String id);
}
