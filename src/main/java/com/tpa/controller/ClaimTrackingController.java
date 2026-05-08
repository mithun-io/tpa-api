package com.tpa.controller;

import com.tpa.entity.ClaimStatusTimeline;
import com.tpa.repository.ClaimStatusTimelineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API for claim status tracking timeline.
 * Also provides WebSocket broadcasting for real-time updates.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class ClaimTrackingController {

    private final ClaimStatusTimelineRepository timelineRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Get full status timeline for a claim.
     * Accessible by the claim owner (CUSTOMER) or staff.
     */
    @GetMapping("/claims/{claimId}/timeline")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'FMG_EMPLOYEE', 'CARRIER_USER', 'CUSTOMER')")
    public ResponseEntity<List<ClaimStatusTimeline>> getTimeline(@PathVariable Long claimId) {
        return ResponseEntity.ok(timelineRepository.findByClaimIdOrderByOccurredAtAsc(claimId));
    }

    /**
     * Push a real-time status update via WebSocket.
     * Called internally by AdminServiceImpl / CarrierServiceImpl on status change.
     * Topic: /topic/claims/{claimId}
     */
    public void broadcastStatusUpdate(Long claimId, String toStatus, String message) {
        Map<String, Object> payload = Map.of(
                "claimId", claimId,
                "status", toStatus,
                "message", message != null ? message : "",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        String destination = "/topic/claims/" + claimId;
        messagingTemplate.convertAndSend(destination, payload);
        log.info("[WS] Broadcast status '{}' for claim {} to '{}'", toStatus, claimId, destination);
    }

    /**
     * Send a private notification to a specific user.
     * Topic: /user/{userEmail}/queue/notifications
     */
    public void sendUserNotification(String userEmail, String title, String message) {
        Map<String, Object> payload = Map.of(
                "title", title,
                "message", message,
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/notifications", payload);
        log.info("[WS] Sent private notification to user '{}'", userEmail);
    }
}
