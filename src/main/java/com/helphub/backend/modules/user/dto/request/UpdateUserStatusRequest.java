package com.helphub.backend.modules.user.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotNull(message = "Status must not be null")
    private Boolean isActive;
}
