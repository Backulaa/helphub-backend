package com.helphub.backend.modules.notification.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnreadNotificationCountResponse {
    private Long unreadCount;
}