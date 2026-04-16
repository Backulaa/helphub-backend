package com.helphub.backend.modules.category.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryStatusRequest {

    @NotNull(message = "isActive is required")
    private Boolean isActive;
}