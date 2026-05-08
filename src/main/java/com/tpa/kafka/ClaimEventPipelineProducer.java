package com.tpa.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.kafka.event.ClaimLifecycleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimEventPipelineProducer {

    private static final String TOPIC_CLAIM_UPLOADED      = "claim-lifecycle.uploaded";
    private static final String TOPIC_OCR_COMPLETED        = "claim-lifecycle.ocr-completed";
    private static final String TOPIC_AI_DONE              = "claim-lifecycle.ai-done";
    private static final String TOPIC_RULE_EVALUATED       = "claim-lifecycle.rule-evaluated";
    private static final String TOPIC_ADMIN_APPROVED       = "claim-lifecycle.admin-approved";
    private static final String TOPIC_CARRIER_APPROVED     = "claim-lifecycle.carrier-approved";
    private static final String TOPIC_PAYMENT_INITIATED    = "claim-lifecycle.payment-initiated";
    private static final String TOPIC_PAYMENT_COMPLETED    = "claim-lifecycle.payment-completed";
    private static final String TOPIC_REJECTED             = "claim-lifecycle.rejected";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishClaimUploaded(ClaimLifecycleEvent event) {
        publish(TOPIC_CLAIM_UPLOADED, event);
    }

    public void publishOcrCompleted(ClaimLifecycleEvent event) {
        publish(TOPIC_OCR_COMPLETED, event);
    }

    public void publishAiAnalysisDone(ClaimLifecycleEvent event) {
        publish(TOPIC_AI_DONE, event);
    }

    public void publishRuleEvaluated(ClaimLifecycleEvent event) {
        publish(TOPIC_RULE_EVALUATED, event);
    }

    public void publishAdminApproved(ClaimLifecycleEvent event) {
        publish(TOPIC_ADMIN_APPROVED, event);
    }

    public void publishCarrierApproved(ClaimLifecycleEvent event) {
        publish(TOPIC_CARRIER_APPROVED, event);
    }

    public void publishPaymentInitiated(ClaimLifecycleEvent event) {
        publish(TOPIC_PAYMENT_INITIATED, event);
    }

    public void publishPaymentCompleted(ClaimLifecycleEvent event) {
        publish(TOPIC_PAYMENT_COMPLETED, event);
    }

    public void publishRejected(ClaimLifecycleEvent event) {
        publish(TOPIC_REJECTED, event);
    }

    private void publish(String topic, ClaimLifecycleEvent event) {
        try {
            String key = "claim-" + event.getClaimId();
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("[KAFKA-PIPELINE] Failed to publish to topic '{}' for claim {}: {}",
                                    topic, event.getClaimId(), ex.getMessage());
                        } else {
                            log.info("[KAFKA-PIPELINE] Published stage '{}' for claim {} to topic '{}'",
                                    event.getStage(), event.getClaimId(), topic);
                        }
                    });
        } catch (Exception e) {
            log.error("[KAFKA-PIPELINE] Serialization error for event {}: {}", event.getEventId(), e.getMessage(), e);
        }
    }
}
