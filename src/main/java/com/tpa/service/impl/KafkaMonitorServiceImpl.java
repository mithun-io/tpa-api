package com.tpa.service.impl;

import com.tpa.dto.response.kafka.*;
import com.tpa.entity.EventAuditLog;
import com.tpa.repository.EventAuditLogRepository;
import com.tpa.service.KafkaMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMonitorServiceImpl implements KafkaMonitorService {

    private final KafkaAdmin kafkaAdmin;

    private final EventAuditLogRepository eventAuditLogRepository;

    private boolean isKafkaReachable() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            adminClient.listTopics()
                    .names()
                    .get();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public KafkaTopicsResponse getTopics() {

        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Set<String> topicNames = adminClient.listTopics().names().get();

            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(topicNames);

            List<KafkaTopicResponse> topics = new ArrayList<>();

            describeTopicsResult.allTopicNames().get().forEach((name, description) -> {
                topics.add(
                        KafkaTopicResponse.builder()
                                .name(name)
                                .partitions(description.partitions().size())
                                .dlq(name.endsWith("-dlq"))
                                .build());
            });

            topics.sort(Comparator.comparing(KafkaTopicResponse::getName));

            long dlqCount = topics.stream()
                            .filter(KafkaTopicResponse::getDlq)
                            .count();

            return KafkaTopicsResponse.builder()
                    .topics(topics)
                    .totalTopics(topics.size())
                    .dlqTopics(dlqCount)
                    .retrievedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("[KAFKA-MONITOR] Failed to fetch Kafka topics", e);
            throw new RuntimeException("Unable to fetch Kafka topics", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public KafkaPipelineHealthResponse getPipelineHealth() {
        long totalEvents = eventAuditLogRepository.count();
        long processedEvents = eventAuditLogRepository.countByProcessedTrue();
        long failedEvents = eventAuditLogRepository.countByProcessedFalse();

        double successRate = totalEvents == 0 ? 100.0 : Math.round(((double) processedEvents / totalEvents) * 10000.0) / 100.0;

        List<EventAuditLog> dlqEntries = eventAuditLogRepository.findByStageOrderByReceivedAtDesc("DLQ");

        KafkaStageBreakdownResponse stageBreakdown = KafkaStageBreakdownResponse.builder()
                        .claimUploaded(eventAuditLogRepository.countByStage("CLAIM_UPLOADED"))
                        .ocrCompleted(eventAuditLogRepository.countByStage("OCR_COMPLETED"))
                        .aiAnalysisDone(eventAuditLogRepository.countByStage("AI_ANALYSIS_DONE"))
                        .ruleEvaluated(eventAuditLogRepository.countByStage("RULE_EVALUATED"))
                        .adminApproved(eventAuditLogRepository.countByStage("ADMIN_APPROVED"))
                        .carrierApproved(eventAuditLogRepository.countByStage("CARRIER_APPROVED"))
                        .paymentInitiated(eventAuditLogRepository.countByStage("PAYMENT_INITIATED"))
                        .paymentCompleted(eventAuditLogRepository.countByStage("PAYMENT_COMPLETED"))
                        .rejected(eventAuditLogRepository.countByStage("REJECTED"))
                        .build();

        return KafkaPipelineHealthResponse.builder()
                .totalEventsProcessed(processedEvents)
                .pendingEvents(failedEvents)
                .totalEvents(totalEvents)
                .successRate(successRate)
                .dlqMessageCount(dlqEntries.size())
                .kafkaStatus(isKafkaReachable() ? "CONNECTED" : "DISCONNECTED")
                .stageBreakdown(stageBreakdown)
                .retrievedAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public KafkaDlqResponse getDlqMessages(int page, int size) {
        List<EventAuditLog> dlqEntries = eventAuditLogRepository.findByStageOrderByReceivedAtDesc("DLQ");

        int total = dlqEntries.size();
        int start = Math.min(page * size, total);
        int end = Math.min(start + size, total);

        return KafkaDlqResponse.builder()
                .dlqMessages(dlqEntries.subList(start, end))
                .totalDlqMessages(total)
                .page(page)
                .size(size)
                .retrievedAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public KafkaPendingEventsResponse getPendingEvents() {
        List<EventAuditLog> pendingEvents = eventAuditLogRepository.findByProcessedFalseOrderByReceivedAtAsc();

        return KafkaPendingEventsResponse.builder()
                .pendingEvents(pendingEvents)
                .count(pendingEvents.size())
                .retrievedAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public KafkaRetryResponse retryDlqEvent(String eventId) {

        Optional<EventAuditLog> eventOptional = eventAuditLogRepository.findAll()
                .stream()
                .filter(event -> eventId.equals(event.getEventId()))
                .findFirst();

        if (eventOptional.isEmpty()) {

            return KafkaRetryResponse.builder()
                    .success(false)
                    .eventId(eventId)
                    .message("DLQ event not found")
                    .retriedAt(LocalDateTime.now())
                    .build();
        }

        EventAuditLog eventAuditLog = eventOptional.get();
        eventAuditLog.setStage("RETRY_QUEUED");
        eventAuditLog.setProcessed(false);

        eventAuditLogRepository.save(eventAuditLog);

        return KafkaRetryResponse.builder()
                .success(true)
                .eventId(eventId)
                .message("Event queued for retry")
                .retriedAt(LocalDateTime.now())
                .build();
    }
}