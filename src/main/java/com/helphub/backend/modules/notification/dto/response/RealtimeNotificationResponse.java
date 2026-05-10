package com.helphub.backend.modules.notification.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeNotificationResponse {
    private String eventType;
    private NotificationResponse notification;
    private Long unreadCount;
}