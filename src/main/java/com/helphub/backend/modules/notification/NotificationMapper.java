package com.helphub.backend.modules.notification;

import com.helphub.backend.modules.notification.dto.response.NotificationResponse;
import com.helphub.backend.persistence.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .actionUrl(notification.getActionUrl())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}