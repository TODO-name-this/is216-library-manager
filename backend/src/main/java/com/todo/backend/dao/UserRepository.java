package com.todo.backend.dao;

import com.todo.backend.entity.User;
import com.todo.backend.entity.identity.UserRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByRole(@RequestParam("role") UserRole role, Pageable pageable);
    Optional<User> findByCccd(@RequestParam("cccd") String cccd);
    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);
}
