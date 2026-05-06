package com.helphub.backend.modules.message.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @Size(max = 2000, message = "Message content must not exceed 2000 characters")
    private String content;

    private List<UUID> mediaIds;
}