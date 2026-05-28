package com.tpa.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.entity.EventAuditLog;
import com.tpa.enums.ClaimStatus;
import com.tpa.helper.EmailService;
import com.tpa.kafka.event.ClaimLifecycleEvent;
import com.tpa.repository.EventAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Pipeline consumer: handles all claim lifecycle stages.
 * Implements idempotency via EventAuditLog.eventId deduplication.
 * Dead Letter Queue: failed messages are written to *-dlq topics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimEventPipelineConsumer {

    private final ObjectMapper objectMapper;
    private final EventAuditLogRepository eventAuditLogRepository;
    private final EmailService emailService;

    // Helpers
    @FunctionalInterface
    interface EventHandler {
        void handle(ClaimLifecycleEvent claimLifecycleEvent);
    }

    private void processEvent(String message, String topic, EventHandler eventHandler) {
        try {
            ClaimLifecycleEvent event = objectMapper.readValue(message, ClaimLifecycleEvent.class);

            // Idempotency check
            if (eventAuditLogRepository.existsByEventId(event.getEventId())) {
                log.warn("[PIPELINE] Duplicate event '{}' detected on topic '{}' — skipping", event.getEventId(), topic);
                return;
            }

            // Persist audit log entry BEFORE processing
            EventAuditLog auditLog = EventAuditLog.builder()
                    .eventId(event.getEventId())
                    .claimId(event.getClaimId())
                    .stage(event.getStage() != null ? event.getStage().name() : "UNKNOWN")
                    .claimStatus(ClaimStatus.valueOf(event.getClaimStatus() != null ? event.getClaimStatus().name() : "UNKNOWN"))
                    .message(event.getMessage())
                    .metadata(event.getMetadata())
                    .topic(topic)
                    .processed(false)
                    .retryCount(event.getRetryCount())
                    .build();
            eventAuditLogRepository.save(auditLog);

            // Execute the stage-specific handler
            eventHandler.handle(event);

            // Mark as processed
            auditLog.setProcessed(true);
            auditLog.setProcessedAt(LocalDateTime.now());
            eventAuditLogRepository.save(auditLog);

        } catch (Exception e) {
            log.error("[PIPELINE] Failed to process event on topic '{}': {} — Message: {}", topic, e.getMessage(), message, e);
            throw new RuntimeException("[PIPELINE] Processing failed — triggering retry: " + e.getMessage(), e);
        }
    }

    private void safeSendEmail(String email, Long claimId, String status, String notes) {
        try {
            if (email != null && !email.isBlank()) {
                emailService.sendClaimStatusNotification(email, claimId, status, notes);
            }
        } catch (Exception e) {
            log.warn("[PIPELINE] Email notification failed for claim {}: {}", claimId, e.getMessage());
        }
    }

    // Stage: CLAIM_UPLOADED
    @KafkaListener(topics = "claim-lifecycle.uploaded", groupId = "tpa-pipeline-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void onClaimUploaded(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        processEvent(message, topic, event -> log.info("[PIPELINE] Claim {} uploaded — ready for OCR/AI processing", event.getClaimId()));
    }

    // Stage: OCR_COMPLETED
    @KafkaListener(topics = "claim-lifecycle.ocr-completed", groupId = "tpa-pipeline-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void onOcrCompleted(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        processEvent(message, topic, event -> log.info("[PIPELINE] OCR completed for claim {} — triggering AI analysis", event.getClaimId()));
    }

    // Stage: AI_ANALYSIS_DONE

    @KafkaListener(topics = "claim-lifecycle.ai-done", groupId = "tpa-pipeline-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void onAiAnalysisDone(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        processEvent(message, topic, event -> {
            log.info("[PIPELINE] AI analysis done for claim {} — status: {}", event.getClaimId(), event.getClaimStatus());
        });
    }

    // Stage: RULE_EVALUATED
    @KafkaListener(topics = "claim-lifecycle.rule-evaluated", groupId = "tpa-pipeline-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void onRuleEvaluated(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        processEvent(message, topic, event -> log.info("[PIPELINE] Rule evaluated for claim {} — outcome: {}", event.getClaimId(), event.getClaimStatus()));
    }

    // Stage: ADMIN_APPROVED
    @KafkaListener(topics = "claim-lifecycle.admin-approved", groupId = "tpa-pipeline-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void onAdminApproved(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        processEvent(message, topic, event -> {
            log.info("[PIPELINE] Admin approved claim {} — notifying customer {}", event.getClaimId(), event.getCustomerEmail());
            safeSendEmail(event.getCustomerEmail(), event.getClaimId(), "ADMIN_APPROVED", event.getMessage());
        });
    }

    // Stage: CARRIER_APPROVED
    @KafkaListener(topics = "claim-lifecycle.carrier-approved", groupId = "tpa-pipeline-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void onCarrierApproved(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        processEvent(message, topic, event -> {
            log.info("[PIPELINE] Carrier approved claim {} — initiating payment", event.getClaimId());
            safeSendEmail(event.getCustomerEmail(), event.getClaimId(), "CARRIER_APPROVED", event.getMessage());
        });
    }

    // Stage: PAYMENT_INITIATED
    @KafkaListener(topics = "claim-lifecycle.payment-initiated", groupId = "tpa-pipeline-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void onPaymentInitiated(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        processEvent(message, topic, event -> log.info("[PIPELINE] Payment initiated for claim {} — amount in metadata: {}", event.getClaimId(), event.getMetadata()));
    }

    // Stage: PAYMENT_COMPLETED
    @KafkaListener(topics = "claim-lifecycle.payment-completed", groupId = "tpa-pipeline-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void onPaymentCompleted(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        processEvent(message, topic, event -> {
            log.info("[PIPELINE] Payment COMPLETED for claim {} — claim SETTLED", event.getClaimId());
            safeSendEmail(event.getCustomerEmail(), event.getClaimId(), "SETTLED", "Your claim payment has been processed successfully.");
        });
    }

    // Stage: REJECTED
    @KafkaListener(topics = "claim-lifecycle.rejected", groupId = "tpa-pipeline-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void onRejected(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        processEvent(message, topic, event -> {
            log.info("[PIPELINE] Claim {} REJECTED — reason: {}", event.getClaimId(), event.getMessage());
            safeSendEmail(event.getCustomerEmail(), event.getClaimId(), "REJECTED", event.getMessage());
        });
    }

    // DLQ Listener
    @KafkaListener(topics = "claim-lifecycle.uploaded-dlq", groupId = "tpa-dlq-group")
    public void onDlq(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("[DLQ] Failed message on topic '{}': {}", topic, message);

        try {
            ClaimLifecycleEvent claimLifecycleEvent = objectMapper.readValue(message, ClaimLifecycleEvent.class);
            EventAuditLog dlqLog = EventAuditLog.builder()
                    .eventId("DLQ-" + claimLifecycleEvent.getEventId())
                    .claimId(claimLifecycleEvent.getClaimId())
                    .stage("DLQ")
                    .claimStatus(ClaimStatus.valueOf(claimLifecycleEvent.getClaimStatus() != null ? claimLifecycleEvent.getClaimStatus().name() : "UNKNOWN"))
                    .message("Message moved to DLQ after max retries")
                    .topic(topic)
                    .processed(false)
                    .retryCount(claimLifecycleEvent.getRetryCount())
                    .errorDetails("Exhausted retries on topic: " + topic)
                    .build();
            eventAuditLogRepository.save(dlqLog);

        } catch (Exception e) {
            log.error("[DLQ] Could not parse DLQ message: {}", e.getMessage());
        }
    }
}
