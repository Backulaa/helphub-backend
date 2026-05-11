package com.helphub.backend.modules.message.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeMessageResponse {
    private String eventType;
    private MessageResponse message;
}