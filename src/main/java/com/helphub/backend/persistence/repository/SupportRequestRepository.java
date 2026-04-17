package com.helphub.backend.persistence.repository;

import com.helphub.backend.common.enums.SupportRequestStatus;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, UUID> {

    List<SupportRequest> findAllByOrderByCreatedAtDesc();

    List<SupportRequest> findAllByStatusOrderByCreatedAtDesc(SupportRequestStatus status);

    List<SupportRequest> findAllByRequesterOrderByCreatedAtDesc(User requester);
}