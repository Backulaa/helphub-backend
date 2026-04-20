package com.helphub.backend.modules.post.dto.request;

import java.util.UUID;

import com.helphub.backend.common.enums.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePostRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Visibility is required")
    private PostVisibility visibility;

    private UUID supportRequestId;
}