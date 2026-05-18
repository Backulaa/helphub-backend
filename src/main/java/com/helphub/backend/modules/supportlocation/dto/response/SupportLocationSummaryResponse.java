package com.helphub.backend.modules.supportlocation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SupportLocationSummaryResponse {
    private UUID id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
    private String contactPhone;
    private Boolean isActive;
    private LocalDateTime createdAt;
}