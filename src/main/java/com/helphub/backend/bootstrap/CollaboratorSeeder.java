package com.helphub.backend.bootstrap;

import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CollaboratorSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedCollaboratorUser() {
        return args -> {

            String collaboratorEmail = "collaborator@helphub.com";

            if (!userRepository.existsByEmail(collaboratorEmail)) {

                log.info("No collaborator user found. Seeding default collaborator user.");

                User collaborator = User.builder()
                        .fullName("Default Collaborator")
                        .email(collaboratorEmail)
                        .password(passwordEncoder.encode("Collaborator@123"))
                        .phone("0987654321")
                        .role(UserRole.COLLABORATOR)
                        .isActive(true)
                        .build();

                userRepository.save(Objects.requireNonNull(collaborator));

                log.info(
                        "Collaborator user created successfully with email: {} and password: {}",
                        collaboratorEmail,
                        "Collaborator@123");
            } else {
                log.info("Collaborator user already exists.");
            }
        };
    }
}