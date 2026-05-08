package com.helphub.backend.modules.conversation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddConversationMemberRequest {

    @NotNull(message = "User id is required")
    private UUID userId;
}