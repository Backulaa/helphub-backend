package com.helphub.backend.modules.postreaction.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PostReactionCountResponse {
    private UUID postId;
    private long totalCount;
    private Map<String, Long> countByType;
}
