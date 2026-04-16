package com.helphub.backend.modules.category.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CategorySummaryResponse {
    private UUID id;
    private String name;
    private String code;
    private String iconUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
}