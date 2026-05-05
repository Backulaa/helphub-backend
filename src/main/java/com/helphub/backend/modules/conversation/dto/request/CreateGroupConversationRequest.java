package com.helphub.backend.modules.conversation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupConversationRequest {

    @NotEmpty(message = "Member ids are required")
    private List<UUID> memberIds;
}