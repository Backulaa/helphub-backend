package com.helphub.backend.modules.conversation.dto.response;

import com.helphub.backend.common.enums.ConversationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSummaryResponse {
    private UUID id;
    private ConversationType type;
    private UUID createdBy;
    private List<ConversationMemberResponse> members;
    private LocalDateTime createdAt;
}