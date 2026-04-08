package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.RefreshToken;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenAndIsRevokedFalse(String token);
}
