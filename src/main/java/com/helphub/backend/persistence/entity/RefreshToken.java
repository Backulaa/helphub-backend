package com.helphub.backend.persistence.entity;

import com.helphub.backend.common.util.DateTimeUtils;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = DateTimeUtils.now();
        if (this.isRevoked == null) {
            this.isRevoked = false;
        }
    }
}