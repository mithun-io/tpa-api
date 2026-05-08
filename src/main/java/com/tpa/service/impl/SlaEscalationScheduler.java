package com.tpa.service.impl;

import com.tpa.entity.Claim;
import com.tpa.kafka.ClaimEventPipelineProducer;
import com.tpa.kafka.event.ClaimLifecycleEvent;
import com.tpa.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scans for SLA breaches every 30 minutes and escalates claims
 * that have passed their SLA deadline without resolution.
 *
 * Escalation criteria:
 * - Claim is in a non-terminal state (not REJECTED, SETTLED, CARRIER_APPROVED)
 * - Claim SLA deadline has passed
 * - Claim is not already escalated
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlaEscalationScheduler {

    private final ClaimRepository claimRepository;
    private final ClaimEventPipelineProducer pipelineProducer;

    private static final List<String> TERMINAL_STATUSES = List.of(
            "REJECTED", "SETTLED", "CARRIER_APPROVED"
    );

    @Scheduled(fixedDelay = 1800000) // every 30 minutes
    @Transactional
    public void runEscalationCheck() {
        LocalDateTime now = LocalDateTime.now();
        log.info("[SLA-ESCALATION] Running SLA breach check at {}", now);

        List<Claim> breachedClaims = claimRepository.findAll().stream()
                .filter(c -> c.getSlaDeadline() != null)
                .filter(c -> c.getSlaDeadline().isBefore(now))
                .filter(c -> Boolean.FALSE.equals(c.getEscalated()))
                .filter(c -> !TERMINAL_STATUSES.contains(c.getStatus() != null ? c.getStatus().name() : ""))
                .toList();

        if (breachedClaims.isEmpty()) {
            log.info("[SLA-ESCALATION] No SLA breaches found");
            return;
        }

        log.warn("[SLA-ESCALATION] Found {} SLA breach(es) — escalating", breachedClaims.size());

        for (Claim claim : breachedClaims) {
            try {
                escalateClaim(claim, now);
            } catch (Exception e) {
                log.error("[SLA-ESCALATION] Failed to escalate claim {}: {}", claim.getId(), e.getMessage());
            }
        }
    }

    private void escalateClaim(Claim claim, LocalDateTime now) {
        long breachHours = java.time.Duration.between(claim.getSlaDeadline(), now).toHours();
        String reason = String.format("SLA breached by %d hours — auto-escalated to senior reviewer", breachHours);

        claim.setEscalated(true);
        claim.setEscalatedAt(now);
        claim.setEscalationReason(reason);
        claimRepository.save(claim);

        // Publish escalation event
        ClaimLifecycleEvent event = ClaimLifecycleEvent.builder()
                .claimId(claim.getId())
                .policyNumber(claim.getPolicyNumber())
                .customerEmail(claim.getUser() != null ? claim.getUser().getEmail() : null)
                .stage(ClaimLifecycleEvent.Stage.RULE_EVALUATED)
                .claimStatus(claim.getStatus())
                .message(reason)
                .metadata("{\"escalated\":true,\"breachHours\":" + breachHours + "}")
                .build();
        pipelineProducer.publishRuleEvaluated(event);

        log.warn("[SLA-ESCALATION] Claim {} escalated — breach: {} hours", claim.getId(), breachHours);
    }
}
