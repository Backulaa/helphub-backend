package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.SupportNeed;
import com.helphub.backend.persistence.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupportNeedRepository extends JpaRepository<SupportNeed, UUID> {

    List<SupportNeed> findAllBySupportRequestOrderByCreatedAtDesc(SupportRequest supportRequest);

    boolean existsBySupportRequestAndNeedNameIgnoreCase(SupportRequest supportRequest, String needName);
}