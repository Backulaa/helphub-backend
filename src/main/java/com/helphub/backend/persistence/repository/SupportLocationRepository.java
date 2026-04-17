package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.SupportLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupportLocationRepository extends JpaRepository<SupportLocation, UUID> {
}