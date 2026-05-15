package com.tpa.service.impl;

import com.tpa.dto.response.auth.NotificationResponse;
import com.tpa.entity.Notification;
import com.tpa.entity.User;
import com.tpa.enums.UserRole;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.repository.NotificationRepository;
import com.tpa.repository.UserRepository;
import com.tpa.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private User getUser(String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> new NoResourceFoundException("User not found"));
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .targetUrl(notification.getTargetUrl())
                .build();
    }

    @Override
    @Transactional
    public void createNotification(User user, String title, String message, String targetUrl) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .targetUrl(targetUrl)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Notification created for user {}", user.getEmail());
    }

    @Override
    @Async("executor")
    @Transactional
    public void notifyAllAdmins(String title, String message, String targetUrl) {

        List<User> admins = userRepository.findAll()
                .stream()
                .filter(user -> UserRole.ADMIN.equals(user.getUserRole()))
                .toList();

        admins.forEach(admin -> createNotification(admin, title, message, targetUrl));
        log.info("Notification sent to {} admins", admins.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(String username) {
        User user = getUser(username);

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(String username) {
        User user = getUser(username);

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .filter(notification -> !notification.isRead())
                .count();
    }

    @Override
    @Transactional
    public void markAllAsRead(String username) {
        User user = getUser(username);

        notificationRepository.markAllAsReadByUserId(user.getId());
        log.info("All notifications marked as read for {}", username);
    }

    @Override
    @Transactional
    public void markOneAsRead(Long notificationId, String username) {
        User user = getUser(username);

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new NoResourceFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to access this notification");
        }

        notification.setRead(true);

        notificationRepository.save(notification);
        log.info("Notification {} marked as read", notificationId);
    }
}