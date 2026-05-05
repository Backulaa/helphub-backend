package com.helphub.backend.modules.conversation.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMemberResponse {
    private UUID userId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private LocalDateTime joinedAt;
}