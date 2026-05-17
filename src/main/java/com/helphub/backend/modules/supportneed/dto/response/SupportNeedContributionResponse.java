package com.helphub.backend.modules.supportneed.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SupportNeedContributionResponse {
    private UUID id;
    private UUID supportNeedId;
    private String needName;
    private UUID contributorId;
    private String contributorName;
    private BigDecimal quantity;
    private String note;
    private LocalDateTime createdAt;
}