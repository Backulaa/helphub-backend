package com.helphub.backend.modules.postcomment.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostCommentRequest {

    @NotBlank(message = "Comment content is required")
    private String content;

    private UUID parentCommentId;
}
