package com.helphub.backend.modules.supportneed;

import com.helphub.backend.modules.supportneed.dto.response.SupportNeedContributionResponse;
import com.helphub.backend.modules.supportneed.dto.response.SupportNeedResponse;
import com.helphub.backend.persistence.entity.SupportNeed;
import com.helphub.backend.persistence.entity.SupportNeedContribution;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SupportNeedMapper {

    public SupportNeedResponse toResponse(SupportNeed supportNeed) {
        BigDecimal requiredQuantity = supportNeed.getRequiredQuantity();
        BigDecimal receivedQuantity = supportNeed.getReceivedQuantity();
        BigDecimal remainingQuantity = requiredQuantity.subtract(receivedQuantity);

        if (remainingQuantity.compareTo(BigDecimal.ZERO) < 0) {
            remainingQuantity = BigDecimal.ZERO;
        }

        return SupportNeedResponse.builder()
                .id(supportNeed.getId())
                .supportRequestId(supportNeed.getSupportRequest().getId())
                .supportRequestTitle(supportNeed.getSupportRequest().getTitle())
                .supportType(supportNeed.getSupportType())
                .needName(supportNeed.getNeedName())
                .unit(supportNeed.getUnit())
                .requiredQuantity(requiredQuantity)
                .receivedQuantity(receivedQuantity)
                .remainingQuantity(remainingQuantity)
                .isFulfilled(receivedQuantity.compareTo(requiredQuantity) >= 0)
                .createdAt(supportNeed.getCreatedAt())
                .updatedAt(supportNeed.getUpdatedAt())
                .build();
    }

    public SupportNeedContributionResponse toContributionResponse(SupportNeedContribution contribution) {
        return SupportNeedContributionResponse.builder()
                .id(contribution.getId())
                .supportNeedId(contribution.getSupportNeed().getId())
                .needName(contribution.getSupportNeed().getNeedName())
                .contributorId(contribution.getContributor().getId())
                .contributorName(contribution.getContributor().getFullName())
                .quantity(contribution.getQuantity())
                .note(contribution.getNote())
                .createdAt(contribution.getCreatedAt())
                .build();
    }
}