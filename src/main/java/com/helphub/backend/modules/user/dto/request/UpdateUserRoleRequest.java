package com.helphub.backend.modules.user.dto.request;

import com.helphub.backend.common.enums.UserRole;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    @NotNull(message = "Role must not be null")
    private UserRole role;
}
