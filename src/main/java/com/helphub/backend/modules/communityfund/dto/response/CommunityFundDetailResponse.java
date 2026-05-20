package com.helphub.backend.modules.communityfund.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CommunityFundDetailResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal totalBalance;
    private Boolean isActive;
    private UUID createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}