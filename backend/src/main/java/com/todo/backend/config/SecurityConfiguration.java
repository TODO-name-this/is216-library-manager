package com.todo.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfiguration {
//    @Bean
    // TODO
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Disable Cross-Site Request Forgery (CSRF) protection
        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(Customizer.withDefaults());

        // Protect endpoints at /api/<type>/secure
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/books/secure/**",
                        "/api/reviews/secure/**",
                        "/api/messages/secure/**",
                        "/api/admin/secure/**")
                .authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        // Add content negotiation strategy
        http.setSharedObject(ContentNegotiationStrategy.class,
                new HeaderContentNegotiationStrategy());

        // Force a non-empty response body for 401's to make the response friendly
        // TODO

        return http.build();
    }
}
