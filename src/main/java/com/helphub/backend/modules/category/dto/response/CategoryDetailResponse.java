package com.helphub.backend.modules.category.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CategoryDetailResponse {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private String iconUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}