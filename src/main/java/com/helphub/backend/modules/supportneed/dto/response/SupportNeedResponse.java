package com.helphub.backend.modules.supportneed.dto.response;

import com.helphub.backend.common.enums.SupportNeedUnit;
import com.helphub.backend.common.enums.SupportType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SupportNeedResponse {
    private UUID id;
    private UUID supportRequestId;
    private String supportRequestTitle;
    private SupportType supportType;
    private String needName;
    private SupportNeedUnit unit;
    private BigDecimal requiredQuantity;
    private BigDecimal receivedQuantity;
    private BigDecimal remainingQuantity;
    private Boolean isFulfilled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}