package com.helphub.backend.modules.notification;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.notification.dto.response.NotificationResponse;
import com.helphub.backend.modules.notification.dto.response.UnreadNotificationCountResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        List<NotificationResponse> response = notificationService.getMyNotifications();

        return ResponseEntity.ok(ApiResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Notifications fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadNotificationCountResponse>> getMyUnreadCount() {
        UnreadNotificationCountResponse response = notificationService.getMyUnreadCount();

        return ResponseEntity.ok(ApiResponse.<UnreadNotificationCountResponse>builder()
                .success(true)
                .message("Unread notification count fetched successfully")
                .data(response)
                .build());
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable @NotNull UUID notificationId) {

        NotificationResponse response = notificationService.markAsRead(notificationId);

        return ResponseEntity.ok(ApiResponse.<NotificationResponse>builder()
                .success(true)
                .message("Notification marked as read successfully")
                .data(response)
                .build());
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Object>> markAllAsRead() {
        notificationService.markAllAsRead();

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("All notifications marked as read successfully")
                .data(null)
                .build());
    }
}