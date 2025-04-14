package com.todo.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {
    @Value("classpath:/firebase-credentials.json")
    private Resource credentials;

    @Bean
    public FirebaseApp firebaseApp() {
        // Initialize Firebase with the credentials file
        try {
            var stream = new ByteArrayInputStream(credentials.getContentAsByteArray());
            var options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(stream))
                .build();

            var app = FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized");
            return app;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null; // fuck java
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
