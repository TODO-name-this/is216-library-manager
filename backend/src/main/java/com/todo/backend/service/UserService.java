package com.todo.backend.service;

import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.user.PartialUpdateUserDto;
import com.todo.backend.dto.user.ResponseUserDto;
import com.todo.backend.dto.user.CreateUserDto;
import com.todo.backend.dto.user.SelfUpdateUserDto;
import com.todo.backend.dto.user.LibrarianUpdateUserDto;
import com.todo.backend.dto.user.AdminUpdateUserDto;
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

    // Role-based user update (for admins and librarians)
    public ResponseUserDto updateUserByRole(String id, LibrarianUpdateUserDto updateUserDto, String currentUserId) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));

        // Get current user's role
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

        UserRole currentUserRole = currentUser.getRole();

        // Role-based restrictions
        if (currentUserRole == UserRole.LIBRARIAN) {
            // Librarians cannot edit admin or librarian users
            if (existingUser.getRole() == UserRole.ADMIN || existingUser.getRole() == UserRole.LIBRARIAN) {
                throw new IllegalArgumentException("Librarians cannot edit admin or librarian users");
            }
        }

        // Apply updates based on current user's role
        if (currentUserRole == UserRole.ADMIN) {
            // Admin can edit everything except other admins, but with role restrictions
            
            // Handle password encoding first
            String encodedPassword = null;
            if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().trim().isEmpty()) {
                encodedPassword = passwordEncoder.encode(updateUserDto.getPassword());
            }
            
            AdminUpdateUserDto adminDto = AdminUpdateUserDto.builder()
                    .cccd(updateUserDto.getCccd())
                    .dob(updateUserDto.getDob())
                    .avatarUrl(updateUserDto.getAvatarUrl())
                    .name(updateUserDto.getName())
                    .phone(updateUserDto.getPhone())
                    .email(updateUserDto.getEmail())
                    .password(encodedPassword) // Use encoded password
                    .role(updateUserDto.getRole())
                    .balance(updateUserDto.getBalance())
                    .build();
            
            // Admin cannot modify other admin users (except themselves)
            if (existingUser.getRole() == UserRole.ADMIN && !existingUser.getId().equals(currentUserId)) {
                throw new IllegalArgumentException("Cannot modify other admin users");
            }
            
            // Role validation: cannot promote to admin
            if (updateUserDto.getRole() == UserRole.ADMIN) {
                throw new IllegalArgumentException("Cannot promote users to admin role");
            }
            
            userMapper.updateEntityFromAdminUpdateDto(adminDto, existingUser);
        } else {
            // Librarian can edit all fields except role, and can set passwords for non-admin users
            // If librarian tries to update role, throw error
            if (updateUserDto.getRole() != null) {
                throw new IllegalArgumentException("Librarians cannot modify user roles");
            }
            
            // Handle password encoding before mapping
            if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().trim().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(updateUserDto.getPassword());
                updateUserDto.setPassword(encodedPassword);
            }
            
            userMapper.updateEntityFromLibrarianUpdateDto(updateUserDto, existingUser);
        }

        // Validate unique constraints
        validateUserUniqueFields(updateUserDto.getEmail(), updateUserDto.getCccd(), id);

        if (updateUserDto.getBalance() != null && updateUserDto.getBalance() < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        userRepository.save(existingUser);
        return userMapper.toResponseDto(existingUser);
    }

    // Self-update for logged-in users (limited fields)
    public ResponseUserDto selfUpdateUser(String id, SelfUpdateUserDto selfUpdateUserDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));

        userMapper.updateEntityFromSelfUpdateDto(selfUpdateUserDto, existingUser);
        
        // Only validate email uniqueness (CCCD and other restricted fields are not editable)
        if (selfUpdateUserDto.getEmail() != null && 
            userRepository.existsByEmailAndIdNot(selfUpdateUserDto.getEmail(), id)) {
            throw new IllegalArgumentException("Email already exists");
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

    private void validateUserUniqueFields(String email, String cccd, String ignoreId) {
        if (email != null && userRepository.existsByEmailAndIdNot(email, ignoreId)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (cccd != null && userRepository.existsByCccdAndIdNot(cccd, ignoreId)) {
            throw new IllegalArgumentException("CCCD already exists");
        }
    }
}
