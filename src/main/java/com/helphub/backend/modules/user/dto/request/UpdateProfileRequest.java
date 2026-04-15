package com.helphub.backend.modules.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 50, message = "Full name must be at most 50 characters")
    private String fullName;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    @Pattern(regexp = "^(\\+?[0-9]{9,15})?$", message = "Phone number is invalid")
    private String phone;

    @Size(max = 1000, message = "Avatar URL must be at most 1000 characters")
    private String avatarUrl;
}
