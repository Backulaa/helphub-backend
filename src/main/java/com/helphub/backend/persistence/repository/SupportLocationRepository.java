package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.SupportLocation;
import com.helphub.backend.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupportLocationRepository extends JpaRepository<SupportLocation, UUID> {

    List<SupportLocation> findAllByOrderByCreatedAtDesc();

    List<SupportLocation> findAllByIsActiveTrueOrderByCreatedAtDesc();

    List<SupportLocation> findAllByCreatedByOrderByCreatedAtDesc(User createdBy);
}