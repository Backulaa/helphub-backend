package com.helphub.backend.modules.postcomment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PostCommentResponse {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private String userName;
    private String userAvatarUrl;
    private UUID parentCommentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}