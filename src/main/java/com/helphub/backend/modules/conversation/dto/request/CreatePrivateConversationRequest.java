package com.helphub.backend.modules.conversation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePrivateConversationRequest {

    @NotNull(message = "Receiver id is required")
    private UUID receiverId;
}