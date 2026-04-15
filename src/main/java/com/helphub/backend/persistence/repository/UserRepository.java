package com.helphub.backend.persistence.repository;

import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.persistence.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndIsActiveTrue(UUID id);

    List<User> findAllByIsActiveTrue();

    Page<User> findByRole(UserRole role, Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCaseAndRole(String fullName, UserRole role, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.id = :userId")
    void updateLastLoginAt(@Param("userId") UUID userId,
            @Param("lastLoginAt") LocalDateTime lastLoginAt);
}
