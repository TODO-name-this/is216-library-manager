package com.todo.backend.config;

import com.todo.backend.entity.identity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true) // Enable method-level security annotations
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UserDetailsService userDetailsService;
    private final JwtRequestFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { http
        // disable cors and csrf
        // we don't need CSRF protection for stateless APIs,
        // and we don't need CORS for this example,
        // we want separate frontend and backend url dipshit
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)

        // disable session management (we want stateless JWT)
        .sessionManagement(sess -> sess.sessionCreationPolicy(STATELESS))

        // jwt auth
        .addFilterBefore(jwtFilter, AuthorizationFilter.class)
        .authenticationProvider(authenticationProvider())

        // Protect endpoints
        .authorizeHttpRequests(auth -> auth
                // public auth endpoints
                .requestMatchers("/api/auth/**").permitAll()

                // TODO: remove all this shit and uses @PreAuthorize in the route/controller
                .requestMatchers(
                        "/api/bookTitle/secure/**",
                        "/api/bookCopy/secure/**",
                        "/api/reviews/secure/**",
                        "/api/messages/secure/**",
                        "/api/admin/secure/**"
                ).authenticated()

                // example of requiring multiple roles.
                // TODO: clean this up
                .requestMatchers(
                        "/api/test/"
                ).hasAnyAuthority(UserRole.LIBRARIAN.name(), UserRole.ADMIN.name())

                // admin only routes
                .requestMatchers("/api/admin/**")
                    .hasAuthority(UserRole.ADMIN.name())

                // block all other requests
                .anyRequest().permitAll()
        );

        // enable dynamically adapting response format based on
        // the client's preferences as expressed in the Accept header.
        // this function returns fucking nothing btw, you cant chain after this. Why?
        http.setSharedObject(ContentNegotiationStrategy.class,
            new HeaderContentNegotiationStrategy()
        );

        return http.build();
    }

    // boilerplates stuff
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder()); // Ensure you have a PasswordEncoder bean
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
