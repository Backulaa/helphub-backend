package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.CommunityFund;
import com.helphub.backend.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommunityFundRepository extends JpaRepository<CommunityFund, UUID> {

    List<CommunityFund> findAllByOrderByCreatedAtDesc();

    List<CommunityFund> findAllByIsActiveTrueOrderByCreatedAtDesc();

    List<CommunityFund> findAllByCreatedByOrderByCreatedAtDesc(User createdBy);
}