package com.helphub.backend.modules.supportrequest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssignSupportRequestToSupportLocationRequest {

    @NotNull(message = "Support location id is required")
    private UUID supportLocationId;
}