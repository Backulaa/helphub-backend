package com.helphub.backend.config;

import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedAdminUser() {
        return args -> {
            String adminEmail = "admin@helphub.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                log.info("No admin user found. Seeding default admin user.");
                User admin = User.builder()
                        .fullName("System Admin")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("Admin@123"))
                        .phone("0123456789")
                        .role(UserRole.ADMIN)
                        .isActive(true)
                        .build();
                userRepository.save(admin);
                log.info("Admin user created successfully with email: {} and password: {}", adminEmail, "Admin@123");
            } else {
                log.info("Admin user already exists.");
            }
        };
    }
}
