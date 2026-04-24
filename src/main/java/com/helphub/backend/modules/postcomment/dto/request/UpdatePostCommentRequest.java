package com.helphub.backend.modules.postcomment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePostCommentRequest {

    @NotBlank(message = "Comment content is required")
    private String content;
}