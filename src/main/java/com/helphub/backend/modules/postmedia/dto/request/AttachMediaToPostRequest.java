package com.helphub.backend.modules.postmedia.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AttachMediaToPostRequest {

    @NotNull(message = "Media id is required")
    private UUID mediaId;

    private Integer displayOrder;
}