package com.helphub.backend.modules.post.dto.response;

import com.helphub.backend.common.enums.PostStatus;
import com.helphub.backend.common.enums.PostVisibility;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PostSummaryResponse {
    private UUID id;
    private UUID authorId;
    private String authorName;
    private String authorAvatarUrl;
    private UUID supportRequestId;
    private String supportRequestTitle;
    private String content;
    private PostVisibility visibility;
    private PostStatus status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}