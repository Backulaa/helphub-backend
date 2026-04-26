package com.helphub.backend.modules.postreaction.dto.response;

import com.helphub.backend.common.enums.PostReactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PostReactionResponse {
    private UUID postId;
    private UUID userId;
    private String userName;
    private PostReactionType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
