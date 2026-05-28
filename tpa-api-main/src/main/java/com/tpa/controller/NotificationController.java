package com.tpa.controller;

import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.auth.NotificationResponse;
import com.tpa.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Notifications fetched successfully", notificationService.getUserNotifications(principal.getName()), 200));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Unread count fetched successfully", Map.of("count", notificationService.countUnread(principal.getName())), 200));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Principal principal) {
        notificationService.markAllAsRead(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "All notifications marked as read", null, 200));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markOneAsRead(@PathVariable Long id, Principal principal) {
        notificationService.markOneAsRead(id, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as read", null, 200));
    }
}