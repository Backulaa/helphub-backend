package com.helphub.backend.modules.notification;

import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.common.util.DateTimeUtils;
import com.helphub.backend.modules.notification.dto.response.NotificationResponse;
import com.helphub.backend.modules.notification.dto.response.UnreadNotificationCountResponse;
import com.helphub.backend.persistence.entity.Notification;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.NotificationRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import com.helphub.backend.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationResponse createNotification(
            UUID userId,
            String content,
            String referenceType,
            UUID referenceId,
            String actionUrl) {
        User user = findActiveUserById(userId);

        Notification notification = Notification.builder()
                .user(user)
                .content(content)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .actionUrl(actionUrl)
                .isRead(false)
                .build();

        notificationRepository.save(Objects.requireNonNull(notification));

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        User currentUser = getCurrentUser();

        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse getMyUnreadCount() {
        User currentUser = getCurrentUser();

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId());

        return UnreadNotificationCountResponse.builder()
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID notificationId) {
        User currentUser = getCurrentUser();

        Notification notification = notificationRepository.findById(Objects.requireNonNull(notificationId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You cannot access this notification");
        }

        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notification.setReadAt(DateTimeUtils.now());
            notificationRepository.save(notification);
        }

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        User currentUser = getCurrentUser();

        List<Notification> notifications = notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(currentUser.getId());

        notifications.forEach(notification -> {
            if (!Boolean.TRUE.equals(notification.getIsRead())) {
                notification.setIsRead(true);
                notification.setReadAt(DateTimeUtils.now());
            }
        });

        notificationRepository.saveAll(notifications);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ForbiddenException("Unauthenticated user");
        }

        return findActiveUserById(userDetails.getUserId());
    }

    private User findActiveUserById(UUID userId) {
        return userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
    }
}