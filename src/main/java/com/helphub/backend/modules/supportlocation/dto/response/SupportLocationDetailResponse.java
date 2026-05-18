package com.helphub.backend.modules.supportlocation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SupportLocationDetailResponse {
    private UUID id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private String contactPhone;
    private UUID createdBy;
    private String createdByName;
    private String bankName;
    private String bankAccountNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}