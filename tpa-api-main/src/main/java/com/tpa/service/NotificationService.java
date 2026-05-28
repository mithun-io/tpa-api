package com.tpa.service;

import com.tpa.dto.response.auth.NotificationResponse;
import com.tpa.entity.User;

import java.util.List;

public interface NotificationService {

    void createNotification(User user, String title, String message, String targetUrl);

    void notifyAllAdmins(String title, String message, String targetUrl);

    List<NotificationResponse> getUserNotifications(String username);

    long countUnread(String username);

    void markAllAsRead(String username);

    void markOneAsRead(Long notificationId, String username);
}