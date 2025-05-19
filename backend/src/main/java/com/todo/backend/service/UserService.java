package com.todo.backend.service;

import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.user.ResponseUserDto;
import com.todo.backend.dto.user.UserDto;
import com.todo.backend.entity.Transaction;
import com.todo.backend.entity.User;
import com.todo.backend.entity.identity.UserRole;
import com.todo.backend.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public ResponseUserDto getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));

        return userMapper.toResponseDto(user);
    }

    public ResponseUserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);

        validateUserRules(user);

        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    public ResponseUserDto updateUser(String id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));

        User newUserData = userMapper.toEntity(userDto);
        validateUserRules(newUserData, existingUser.getId());

        userMapper.updateEntityFromDto(userDto, existingUser);

        userRepository.save(existingUser);
        return userMapper.toResponseDto(existingUser);
    }

    public void deleteUser(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));

        if (existingUser.getRole() == UserRole.ADMIN) {
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

    private void validateUserRules(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByCccd(user.getCccd())) {
            throw new IllegalArgumentException("CCCD already exists");
        }

        if (user.getBalance() < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
    }

    private void validateUserRules(User user, String ignoreId) {
        if (userRepository.existsByEmailAndIdNot(user.getEmail(), ignoreId)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByCccdAndIdNot(user.getCccd(), ignoreId)) {
            throw new IllegalArgumentException("CCCD already exists");
        }

        if (user.getBalance() < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
    }
}
