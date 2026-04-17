package com.helphub.backend.modules.supportrequest.dto.response;

import com.helphub.backend.common.enums.SupportRequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SupportRequestDetailResponse {
    private UUID id;
    private String title;
    private String description;
    private UUID categoryId;
    private String categoryName;
    private UUID requesterId;
    private String requesterName;
    private UUID assignedSupportLocationId;
    private String assignedSupportLocationName;
    private SupportRequestStatus status;
    private Double latitude;
    private Double longitude;
    private String address;
    private UUID reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}