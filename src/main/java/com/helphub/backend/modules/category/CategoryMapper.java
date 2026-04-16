package com.helphub.backend.modules.category;

import com.helphub.backend.modules.category.dto.response.CategoryDetailResponse;
import com.helphub.backend.modules.category.dto.response.CategorySummaryResponse;
import com.helphub.backend.persistence.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategorySummaryResponse toSummaryResponse(Category category) {
        return CategorySummaryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .iconUrl(category.getIconUrl())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .build();
    }

    public CategoryDetailResponse toDetailResponse(Category category) {
        return CategoryDetailResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}