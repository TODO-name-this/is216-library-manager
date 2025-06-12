package com.todo.backend.service;

import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.user.PartialUpdateUserDto;
import com.todo.backend.dto.user.ResponseUserDto;
import com.todo.backend.dto.user.CreateUserDto;
import com.todo.backend.entity.Transaction;
import com.todo.backend.entity.User;
import com.todo.backend.entity.identity.UserRole;
import com.todo.backend.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<ResponseUserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseDtoList(users);
    }

    public List<ResponseUserDto> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            // If no query provided, return all users
            List<User> users = userRepository.findAll();
            return userMapper.toResponseDtoList(users);
        }
        
        String trimmedQuery = query.trim();
        
        // Priority 1: Exact CCCD match
        Optional<User> exactCccdMatch = userRepository.findByCccd(trimmedQuery);
        if (exactCccdMatch.isPresent()) {
            return List.of(userMapper.toResponseDto(exactCccdMatch.get()));
        }
        
        // Priority 2: Exact name match (case insensitive)
        List<User> exactNameMatches = userRepository.findByNameIgnoreCase(trimmedQuery);
        if (!exactNameMatches.isEmpty()) {
            return userMapper.toResponseDtoList(exactNameMatches);
        }
        
        // Priority 3: Exact email match (case insensitive)
        List<User> exactEmailMatches = userRepository.findByEmailIgnoreCase(trimmedQuery);
        if (!exactEmailMatches.isEmpty()) {
            return userMapper.toResponseDtoList(exactEmailMatches);
        }
        
        // Priority 4: Partial CCCD match
        List<User> partialCccdMatches = userRepository.findByCccdContainingIgnoreCase(trimmedQuery);
        if (!partialCccdMatches.isEmpty()) {
            return userMapper.toResponseDtoList(partialCccdMatches);
        }
        
        // Priority 5: Partial name match
        List<User> partialNameMatches = userRepository.findByNameContainingIgnoreCase(trimmedQuery);
        if (!partialNameMatches.isEmpty()) {
            return userMapper.toResponseDtoList(partialNameMatches);
        }
        
        // Priority 6: Partial email match
        List<User> partialEmailMatches = userRepository.findByEmailContainingIgnoreCase(trimmedQuery);
        return userMapper.toResponseDtoList(partialEmailMatches);
    }

    public ResponseUserDto getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));

        return userMapper.toResponseDto(user);
    }

    public ResponseUserDto createUser(CreateUserDto createUserDto) {
        User user = userMapper.toEntity(createUserDto);

        validateUserRules(user);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    public ResponseUserDto updateUser(String id, PartialUpdateUserDto partialUpdateUserDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));

        userMapper.updateEntityFromDto(partialUpdateUserDto, existingUser);
        validateUserRules(existingUser, existingUser.getId());

        String newPassword = partialUpdateUserDto.getNewPassword();
        String oldPassword = partialUpdateUserDto.getOldPassword();

        if (newPassword != null && !newPassword.isBlank()) {
            if (oldPassword == null || oldPassword.isBlank()) {
                throw new IllegalArgumentException("Old password is required when setting a new password");
            }

            if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
                throw new IllegalArgumentException("Old password is incorrect");
            }

            String hashedNewPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(hashedNewPassword);
        }

        userRepository.save(existingUser);
        return userMapper.toResponseDto(existingUser);
    }

    public void deleteUser(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));

        List<Transaction> transactions = existingUser.getTransactions();
        boolean hasUnreturnedTransactions = transactions.stream()
                .anyMatch(t -> t.getReturnedDate() == null);

        if (hasUnreturnedTransactions) {
            throw new IllegalArgumentException("User has unreturned transactions and cannot be deleted");
        }

        userRepository.delete(existingUser);
    }

    public boolean canDeleteUser(String deleteUserId, String currentUserId) {
        if (deleteUserId == null || deleteUserId.isBlank() || currentUserId == null || currentUserId.isBlank() ) {
            return false;
        }

        if (deleteUserId.equals(currentUserId)) {
            return false;
        }

        UserRole currentUserRole = userRepository.findById(currentUserId)
                .map(User::getRole)
                .orElse(UserRole.USER);

        User deleteUser = userRepository.findById(deleteUserId).orElse(null);
        if (deleteUser == null) {
            return false;
        }

        if (currentUserRole == UserRole.ADMIN) {
            return true;
        } else if (currentUserRole == UserRole.LIBRARIAN) {
            return deleteUser.getRole() != UserRole.ADMIN && deleteUser.getRole() != UserRole.LIBRARIAN;
        }

        return false;
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
