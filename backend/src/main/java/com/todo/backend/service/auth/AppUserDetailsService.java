package com.todo.backend.service.auth;

import com.todo.backend.dao.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
// fuck java and its inability to fucking rename your types
// even fucking C++ has type alias. it is fucking 42+ years old.
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // use id as username
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        // yes you cant import User entity. that's why it's var. It has to be var. Why?
        var userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with cccd: " + id));

        var authorities = List.of(
                new SimpleGrantedAuthority(userEntity.getRole().name())
        );

        return new User(userEntity.getCccd(), userEntity.getPassword(), authorities);
    }
}
