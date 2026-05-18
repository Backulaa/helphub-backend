package com.helphub.backend.modules.supportlocation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSupportLocationStatusRequest {

    @NotNull(message = "Active status is required")
    private Boolean isActive;
}