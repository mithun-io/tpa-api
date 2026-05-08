package com.tpa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.entity.EventAuditLog;
import com.tpa.repository.EventAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Admin Kafka Pipeline Monitor Controller
 * Provides real-time visibility into the Kafka claim lifecycle pipeline:
 * - Topic list and partition info
 * - Consumer group lag per topic (unprocessed events)
 * - DLQ events (failed messages after max retries)
 * - Pipeline health summary
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/kafka")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FMG_ADMIN')")
public class KafkaMonitorController {

    private final KafkaAdmin kafkaAdmin;
    private final EventAuditLogRepository eventAuditLogRepository;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // ── Topic Metadata ────────────────────────────────────────────────────────

    @GetMapping("/topics")
    public ResponseEntity<Map<String, Object>> getTopics() {
        Map<String, Object> result = new LinkedHashMap<>();
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            ListTopicsResult topicsResult = adminClient.listTopics();
            Set<String> topicNames = topicsResult.names().get();

            List<Map<String, Object>> topicInfo = new ArrayList<>();
            DescribeTopicsResult descResult = adminClient.describeTopics(topicNames);
            descResult.allTopicNames().get().forEach((name, desc) -> {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("name", name);
                info.put("partitions", desc.partitions().size());
                info.put("isDlq", name.endsWith("-dlq"));
                topicInfo.add(info);
            });

            topicInfo.sort(Comparator.comparing(t -> t.get("name").toString()));
            result.put("topics", topicInfo);
            result.put("totalTopics", topicInfo.size());
            result.put("dlqTopics", topicInfo.stream().filter(t -> (Boolean) t.get("isDlq")).count());
            result.put("retrievedAt", LocalDateTime.now().toString());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[KAFKA-MONITOR] Failed to retrieve topic list: {}", e.getMessage());
            result.put("error", "Unable to connect to Kafka: " + e.getMessage());
            result.put("status", "DEGRADED");
            result.put("retrievedAt", LocalDateTime.now().toString());
            return ResponseEntity.ok(result);
        }
    }

    // ── Pipeline Health Summary ───────────────────────────────────────────────

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getPipelineHealth() {
        Map<String, Object> result = new LinkedHashMap<>();

        // Processed vs unprocessed from DB audit logs
        long totalEvents = eventAuditLogRepository.count();
        long processedEvents = eventAuditLogRepository.countByProcessedTrue();
        long failedEvents = eventAuditLogRepository.countByProcessedFalse();

        result.put("totalEventsProcessed", processedEvents);
        result.put("pendingEvents", failedEvents);
        result.put("totalEvents", totalEvents);
        result.put("successRate", totalEvents == 0 ? 100.0 :
                Math.round((double) processedEvents / totalEvents * 100 * 100.0) / 100.0);

        // DLQ entries
        List<EventAuditLog> dlqEntries = eventAuditLogRepository.findByStageOrderByReceivedAtDesc("DLQ");
        result.put("dlqMessageCount", dlqEntries.size());

        // Per-stage breakdown
        List<String> stages = List.of("CLAIM_UPLOADED", "OCR_COMPLETED", "AI_ANALYSIS_DONE",
                "RULE_EVALUATED", "ADMIN_APPROVED", "CARRIER_APPROVED",
                "PAYMENT_INITIATED", "PAYMENT_COMPLETED", "REJECTED");

        Map<String, Long> stageBreakdown = new LinkedHashMap<>();
        for (String stage : stages) {
            stageBreakdown.put(stage, eventAuditLogRepository.countByStage(stage));
        }
        result.put("stageBreakdown", stageBreakdown);

        // Connection status
        result.put("kafkaStatus", isKafkaReachable() ? "CONNECTED" : "DISCONNECTED");
        result.put("retrievedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    // ── DLQ Messages ──────────────────────────────────────────────────────────

    @GetMapping("/dlq")
    public ResponseEntity<Map<String, Object>> getDlqMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<EventAuditLog> dlqEntries = eventAuditLogRepository.findByStageOrderByReceivedAtDesc("DLQ");
        int total = dlqEntries.size();
        int start = Math.min(page * size, total);
        int end = Math.min(start + size, total);

        result.put("dlqMessages", dlqEntries.subList(start, end));
        result.put("totalDlqMessages", total);
        result.put("page", page);
        result.put("size", size);
        result.put("retrievedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    // ── Unprocessed Events ────────────────────────────────────────────────────

    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingEvents() {
        List<EventAuditLog> pending = eventAuditLogRepository.findByProcessedFalseOrderByReceivedAtAsc();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pendingEvents", pending);
        result.put("count", pending.size());
        result.put("retrievedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    // ── Event Replay (trigger reprocessing) ──────────────────────────────────

    @PostMapping("/dlq/{eventId}/retry")
    public ResponseEntity<Map<String, Object>> retryDlqEvent(@PathVariable String eventId) {
        Map<String, Object> result = new LinkedHashMap<>();
        Optional<EventAuditLog> logOpt = eventAuditLogRepository.findAll().stream()
                .filter(e -> eventId.equals(e.getEventId()))
                .findFirst();

        if (logOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "DLQ event not found: " + eventId);
            return ResponseEntity.ok(result);
        }

        EventAuditLog dlqEvent = logOpt.get();
        dlqEvent.setStage("RETRY_QUEUED");
        dlqEvent.setProcessed(false);
        eventAuditLogRepository.save(dlqEvent);

        result.put("success", true);
        result.put("eventId", eventId);
        result.put("message", "Event queued for retry");
        result.put("retriedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isKafkaReachable() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.listTopics().names().get(java.util.concurrent.TimeUnit.SECONDS.toMillis(3),
                    java.util.concurrent.TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
