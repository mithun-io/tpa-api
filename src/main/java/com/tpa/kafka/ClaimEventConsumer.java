package com.tpa.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.dto.request.ClaimDataRequest;
import com.tpa.dto.response.ClaimDecisionResponse;
import com.tpa.dto.response.ClaimResponse;
import com.tpa.helper.EmailService;
import com.tpa.kafka.event.ClaimLifecycleEvent;
import com.tpa.service.ClaimService;
import com.tpa.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimEventConsumer {

    private final ObjectMapper objectMapper;
    private final RuleEngineService ruleEngineService;
    private final ClaimService claimService;
    private final EmailService emailService;
    private final ClaimEventPipelineProducer pipelineProducer;

    @KafkaListener(topics = "claim-created", groupId = "tpa-group")
    public void consumeClaimCreatedEvent(String message) {
        log.info("[KAFKA] Received claim-created event: {}", message);
        try {
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            Long claimId = Long.valueOf(event.get("claimId").toString());

            ClaimResponse claimResponse = claimService.getClaim(claimId);
            if (claimResponse.getClaimStatus() == com.tpa.enums.ClaimStatus.CARRIER_APPROVED ||
                claimResponse.getClaimStatus() == com.tpa.enums.ClaimStatus.SETTLED ||
                claimResponse.getClaimStatus() == com.tpa.enums.ClaimStatus.REJECTED) {
                log.info("[KAFKA] Claim {} already finalized with {}. Skipping.", claimId, claimResponse.getClaimStatus());
                return;
            }

            String dataJson = objectMapper.writeValueAsString(event.get("data"));
            ClaimDataRequest request = objectMapper.readValue(dataJson, ClaimDataRequest.class);

            log.info("[KAFKA] Running rule engine for claim {}", claimId);

            // Pass claimId for audit logging
            ClaimDecisionResponse decision = ruleEngineService.evaluateClaim(request, claimId, false);
            log.info("[KAFKA] Rule engine decision for claim {}: status={}, reasons={}", claimId, decision.getStatus(), decision.getReasons());

            claimService.processClaimDecision(claimId, decision);
            log.info("[KAFKA] Claim {} status updated to {}", claimId, decision.getStatus());

            // Publish to lifecycle pipeline
            ClaimLifecycleEvent lifecycleEvent = ClaimLifecycleEvent.builder()
                    .claimId(claimId)
                    .policyNumber(claimResponse.getPolicyNumber())
                    .customerEmail(claimResponse.getUserEmail())
                    .stage(ClaimLifecycleEvent.Stage.RULE_EVALUATED)
                    .claimStatus(decision.getStatus())
                    .message("Rule engine decision: " + String.join(", ", decision.getReasons()))
                    .build();
            pipelineProducer.publishRuleEvaluated(lifecycleEvent);

        } catch (Exception e) {
            log.error("[KAFKA] Error processing claim event. Message: {}. Error: {}", message, e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "claim-notifications", groupId = "tpa-group")
    public void consumeClaimNotificationEvent(String message) {
        try {
            log.info("Received claim-notification event: {}", message);
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});

            Long claimId = Long.valueOf(event.get("claimId").toString());
            String status = event.get("status").toString();
            String reviewNotes = event.get("message").toString();

            String customerEmail = event.get("customerEmail") != null
                    ? event.get("customerEmail").toString() : "customer-" + claimId + "@tpa.com";

            emailService.sendClaimStatusNotification(customerEmail, claimId, status, reviewNotes);
            log.info("Sent email notification for claim {} to {}", claimId, customerEmail);

        } catch (Exception e) {
            log.error("Error processing claim notification: {}", e.getMessage());
        }
    }
}
