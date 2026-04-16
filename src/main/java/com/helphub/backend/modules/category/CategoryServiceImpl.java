package com.helphub.backend.modules.category;

import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.category.dto.request.CreateCategoryRequest;
import com.helphub.backend.modules.category.dto.request.UpdateCategoryRequest;
import com.helphub.backend.modules.category.dto.request.UpdateCategoryStatusRequest;
import com.helphub.backend.modules.category.dto.response.CategoryDetailResponse;
import com.helphub.backend.modules.category.dto.response.CategorySummaryResponse;
import com.helphub.backend.persistence.entity.Category;
import com.helphub.backend.persistence.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDetailResponse createCategory(CreateCategoryRequest request) {
        String normalizedName = normalizeText(request.getName());
        String normalizedCode = normalizeCode(request.getCode());

        validateDuplicate(null, normalizedName, normalizedCode);

        Category category = Category.builder()
                .name(normalizedName)
                .code(normalizedCode)
                .description(normalizeNullableText(request.getDescription()))
                .iconUrl(normalizeNullableText(request.getIconUrl()))
                .isActive(true)
                .build();

        categoryRepository.save(Objects.requireNonNull(category));
        return categoryMapper.toDetailResponse(category);
    }

    @Override
    public List<CategorySummaryResponse> getActiveCategories() {
        return categoryRepository.findAllByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(categoryMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public List<CategorySummaryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(categoryMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public CategoryDetailResponse getCategoryById(UUID id) {
        Category category = findCategoryById(Objects.requireNonNull(id));
        return categoryMapper.toDetailResponse(category);
    }

    @Override
    public CategoryDetailResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        Category category = findCategoryById(Objects.requireNonNull(id));

        String normalizedName = normalizeText(request.getName());
        String normalizedCode = normalizeCode(request.getCode());

        validateDuplicate(id, normalizedName, normalizedCode);

        category.setName(normalizedName);
        category.setCode(normalizedCode);
        category.setDescription(normalizeNullableText(request.getDescription()));
        category.setIconUrl(normalizeNullableText(request.getIconUrl()));

        categoryRepository.save(category);
        return categoryMapper.toDetailResponse(category);
    }

    @Override
    public CategoryDetailResponse updateCategoryStatus(UUID id, UpdateCategoryStatusRequest request) {
        Category category = findCategoryById(Objects.requireNonNull(id));
        category.setIsActive(request.getIsActive());
        categoryRepository.save(category);
        return categoryMapper.toDetailResponse(category);
    }

    private Category findCategoryById(@NonNull UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private void validateDuplicate(UUID currentId, String name, String code) {
        categoryRepository.findAll().forEach(category -> {
            boolean sameRecord = currentId != null && category.getId().equals(currentId);

            if (!sameRecord && category.getName() != null && category.getName().equalsIgnoreCase(name)) {
                throw new BadRequestException("Category name already exists");
            }

            if (!sameRecord && category.getCode() != null && category.getCode().equalsIgnoreCase(code)) {
                throw new BadRequestException("Category code already exists");
            }
        });
    }

    private String normalizeText(String input) {
        return input == null ? null : input.trim();
    }

    private String normalizeNullableText(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeCode(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().toUpperCase().replace(" ", "_");
    }
}