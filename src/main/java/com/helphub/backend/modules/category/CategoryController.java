package com.helphub.backend.modules.category;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.category.dto.request.CreateCategoryRequest;
import com.helphub.backend.modules.category.dto.request.UpdateCategoryRequest;
import com.helphub.backend.modules.category.dto.request.UpdateCategoryStatusRequest;
import com.helphub.backend.modules.category.dto.response.CategoryDetailResponse;
import com.helphub.backend.modules.category.dto.response.CategorySummaryResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryDetailResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CategoryDetailResponse>builder()
                        .success(true)
                        .message("Category created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>> getCategories(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        List<CategorySummaryResponse> response = activeOnly
                ? categoryService.getActiveCategories()
                : categoryService.getAllCategories();

        return ResponseEntity.ok(ApiResponse.<List<CategorySummaryResponse>>builder()
                .success(true)
                .message("Categories fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> getCategoryById(@PathVariable @NotNull UUID id) {
        CategoryDetailResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.<CategoryDetailResponse>builder()
                .success(true)
                .message("Category fetched successfully")
                .data(response)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> updateCategory(
            @PathVariable @NotNull UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryDetailResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.<CategoryDetailResponse>builder()
                .success(true)
                .message("Category updated successfully")
                .data(response)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> updateCategoryStatus(
            @PathVariable @NotNull UUID id,
            @Valid @RequestBody UpdateCategoryStatusRequest request) {
        CategoryDetailResponse response = categoryService.updateCategoryStatus(id, request);
        return ResponseEntity.ok(ApiResponse.<CategoryDetailResponse>builder()
                .success(true)
                .message("Category status updated successfully")
                .data(response)
                .build());
    }
}