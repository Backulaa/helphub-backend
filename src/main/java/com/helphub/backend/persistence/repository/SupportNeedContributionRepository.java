package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.SupportNeed;
import com.helphub.backend.persistence.entity.SupportNeedContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupportNeedContributionRepository extends JpaRepository<SupportNeedContribution, UUID> {

    List<SupportNeedContribution> findAllBySupportNeedOrderByCreatedAtDesc(SupportNeed supportNeed);
}