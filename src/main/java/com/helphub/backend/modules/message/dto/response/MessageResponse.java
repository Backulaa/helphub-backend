package com.helphub.backend.modules.message.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private UUID conversationId;
    private UUID senderId;
    private String senderName;
    private String senderAvatarUrl;
    private String content;
    private List<MessageMediaResponse> media;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime editedAt;
}