package com.helphub.backend.modules.supportrequest;

import com.helphub.backend.modules.supportrequest.dto.response.SupportRequestDetailResponse;
import com.helphub.backend.modules.supportrequest.dto.response.SupportRequestSummaryResponse;
import com.helphub.backend.persistence.entity.SupportLocation;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import org.springframework.stereotype.Component;

@Component
public class SupportRequestMapper {

    public SupportRequestSummaryResponse toSummaryResponse(SupportRequest supportRequest) {
        User requester = supportRequest.getRequester();

        return SupportRequestSummaryResponse.builder()
                .id(supportRequest.getId())
                .title(supportRequest.getTitle())
                .categoryId(supportRequest.getCategory().getId())
                .categoryName(supportRequest.getCategory().getName())
                .requesterId(requester.getId())
                .requesterName(requester.getFullName())
                .status(supportRequest.getStatus())
                .address(supportRequest.getAddress())
                .latitude(supportRequest.getLatitude())
                .longitude(supportRequest.getLongitude())
                .createdAt(supportRequest.getCreatedAt())
                .build();
    }

    public SupportRequestDetailResponse toDetailResponse(SupportRequest supportRequest) {
        SupportLocation supportLocation = supportRequest.getAssignedSupportLocation();
        User reviewer = supportRequest.getReviewedBy();

        return SupportRequestDetailResponse.builder()
                .id(supportRequest.getId())
                .title(supportRequest.getTitle())
                .description(supportRequest.getDescription())
                .categoryId(supportRequest.getCategory().getId())
                .categoryName(supportRequest.getCategory().getName())
                .requesterId(supportRequest.getRequester().getId())
                .requesterName(supportRequest.getRequester().getFullName())
                .assignedSupportLocationId(supportLocation != null ? supportLocation.getId() : null)
                .assignedSupportLocationName(supportLocation != null ? supportLocation.getName() : null)
                .status(supportRequest.getStatus())
                .latitude(supportRequest.getLatitude())
                .longitude(supportRequest.getLongitude())
                .address(supportRequest.getAddress())
                .reviewedBy(reviewer != null ? reviewer.getId() : null)
                .reviewedAt(supportRequest.getReviewedAt())
                .rejectionReason(supportRequest.getRejectionReason())
                .createdAt(supportRequest.getCreatedAt())
                .updatedAt(supportRequest.getUpdatedAt())
                .build();
    }
}