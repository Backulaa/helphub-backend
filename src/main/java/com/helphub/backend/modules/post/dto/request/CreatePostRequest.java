package com.helphub.backend.modules.post.dto.request;

import java.util.UUID;

import com.helphub.backend.common.enums.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 2000, message = "Content must not exceed 2000 characters")
    private String content;

    @NotNull(message = "Visibility is required")
    private PostVisibility visibility;

    private UUID supportRequestId;
}