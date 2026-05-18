package com.helphub.backend.modules.supportlocation;

import com.helphub.backend.modules.supportlocation.dto.response.SupportLocationDetailResponse;
import com.helphub.backend.modules.supportlocation.dto.response.SupportLocationSummaryResponse;
import com.helphub.backend.persistence.entity.SupportLocation;
import org.springframework.stereotype.Component;

@Component
public class SupportLocationMapper {

    public SupportLocationSummaryResponse toSummaryResponse(SupportLocation supportLocation) {
        return SupportLocationSummaryResponse.builder()
                .id(supportLocation.getId())
                .name(supportLocation.getName())
                .latitude(supportLocation.getLatitude())
                .longitude(supportLocation.getLongitude())
                .address(supportLocation.getAddress())
                .contactPhone(supportLocation.getContactPhone())
                .isActive(supportLocation.getIsActive())
                .createdAt(supportLocation.getCreatedAt())
                .build();
    }

    public SupportLocationDetailResponse toDetailResponse(SupportLocation supportLocation) {
        return SupportLocationDetailResponse.builder()
                .id(supportLocation.getId())
                .name(supportLocation.getName())
                .description(supportLocation.getDescription())
                .latitude(supportLocation.getLatitude())
                .longitude(supportLocation.getLongitude())
                .address(supportLocation.getAddress())
                .contactPhone(supportLocation.getContactPhone())
                .createdBy(supportLocation.getCreatedBy().getId())
                .createdByName(supportLocation.getCreatedBy().getFullName())
                .bankName(supportLocation.getBankName())
                .bankAccountNumber(supportLocation.getBankAccountNumber())
                .isActive(supportLocation.getIsActive())
                .createdAt(supportLocation.getCreatedAt())
                .updatedAt(supportLocation.getUpdatedAt())
                .build();
    }
}