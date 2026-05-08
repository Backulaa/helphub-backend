package com.helphub.backend.modules.notification;

import com.helphub.backend.modules.notification.dto.response.NotificationResponse;
import com.helphub.backend.modules.notification.dto.response.UnreadNotificationCountResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    NotificationResponse createNotification(
            UUID userId,
            String content,
            String referenceType,
            UUID referenceId,
            String actionUrl);

    List<NotificationResponse> getMyNotifications();

    UnreadNotificationCountResponse getMyUnreadCount();

    NotificationResponse markAsRead(UUID notificationId);

    void markAllAsRead();
}