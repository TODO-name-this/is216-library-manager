package com.todo.backend.dao;

import com.todo.backend.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByRole(@RequestParam("role") String role, Pageable pageable);

    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);
}
