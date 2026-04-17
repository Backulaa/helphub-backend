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
public class SupportRequestSummaryResponse {
    private UUID id;
    private String title;
    private String categoryName;
    private UUID categoryId;
    private UUID requesterId;
    private String requesterName;
    private SupportRequestStatus status;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
}