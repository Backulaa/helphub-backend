package com.helphub.backend.modules.user.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.helphub.backend.common.enums.UserRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private String avatarUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}