package com.tpa.controller;

import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.kafka.KafkaDlqResponse;
import com.tpa.dto.response.kafka.KafkaPendingEventsResponse;
import com.tpa.dto.response.kafka.KafkaPipelineHealthResponse;
import com.tpa.dto.response.kafka.KafkaRetryResponse;
import com.tpa.dto.response.kafka.KafkaTopicsResponse;
import com.tpa.service.KafkaMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/kafka")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class KafkaMonitorController {

    private final KafkaMonitorService kafkaMonitorService;

    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<KafkaTopicsResponse>> getTopics() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Kafka topics fetched successfully", kafkaMonitorService.getTopics(), 200));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<KafkaPipelineHealthResponse>> getPipelineHealth() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Kafka pipeline health fetched successfully", kafkaMonitorService.getPipelineHealth(), 200));
    }

    @GetMapping("/dlq")
    public ResponseEntity<ApiResponse<KafkaDlqResponse>> getDlqMessages(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(new ApiResponse<>(true, "DLQ messages fetched successfully", kafkaMonitorService.getDlqMessages(page, size), 200));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<KafkaPendingEventsResponse>> getPendingEvents() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending Kafka events fetched successfully", kafkaMonitorService.getPendingEvents(), 200));
    }

    @PostMapping("/dlq/{eventId}/retry")
    public ResponseEntity<ApiResponse<KafkaRetryResponse>> retryDlqEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "DLQ retry initiated successfully", kafkaMonitorService.retryDlqEvent(eventId), 200));
    }
}