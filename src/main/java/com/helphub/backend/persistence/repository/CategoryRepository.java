package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    Optional<Category> findByCodeIgnoreCase(String code);

    List<Category> findAllByIsActiveTrueOrderByCreatedAtDesc();

    List<Category> findAllByOrderByCreatedAtDesc();
}