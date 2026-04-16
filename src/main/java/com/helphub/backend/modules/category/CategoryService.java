package com.helphub.backend.modules.category;

import com.helphub.backend.modules.category.dto.request.CreateCategoryRequest;
import com.helphub.backend.modules.category.dto.request.UpdateCategoryRequest;
import com.helphub.backend.modules.category.dto.request.UpdateCategoryStatusRequest;
import com.helphub.backend.modules.category.dto.response.CategoryDetailResponse;
import com.helphub.backend.modules.category.dto.response.CategorySummaryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryDetailResponse createCategory(CreateCategoryRequest request);

    List<CategorySummaryResponse> getActiveCategories();

    List<CategorySummaryResponse> getAllCategories();

    CategoryDetailResponse getCategoryById(UUID id);

    CategoryDetailResponse updateCategory(UUID id, UpdateCategoryRequest request);

    CategoryDetailResponse updateCategoryStatus(UUID id, UpdateCategoryStatusRequest request);
}