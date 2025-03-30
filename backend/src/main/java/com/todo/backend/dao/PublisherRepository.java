package com.todo.backend.dao;

import com.todo.backend.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, String> {
}
