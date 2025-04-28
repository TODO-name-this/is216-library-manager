package com.todo.backend.service;

import com.todo.backend.dao.UserRepository;
import com.todo.backend.entity.Transaction;
import com.todo.backend.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User with this ID already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByCccd(user.getCccd())) {
            throw new IllegalArgumentException("CCCD already exists");
        }

        return userRepository.save(user);
    }

    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User with this ID does not exist");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByCccd(user.getCccd())) {
            throw new IllegalArgumentException("CCCD already exists");
        }
        
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));

        if (existingUser.getRole().equals("ADMIN")) {
            throw new IllegalArgumentException("Cannot delete admin user");
        }

        List<Transaction> transactions = existingUser.getTransactions();
        boolean hasUnreturnedTransactions = transactions.stream()
                .filter(t -> t.getTransactionDetails() != null && !t.getTransactionDetails().isEmpty())
                .flatMap(t -> t.getTransactionDetails().stream())
                .anyMatch(td -> td.getReturnedDate() == null);

        if (hasUnreturnedTransactions) {
            throw new IllegalArgumentException("User has unreturned transactions and cannot be deleted");
        }

        userRepository.delete(existingUser);
    }
}
