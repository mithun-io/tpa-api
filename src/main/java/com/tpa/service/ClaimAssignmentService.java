package com.tpa.service;

import com.tpa.entity.Claim;
import com.tpa.entity.User;
import com.tpa.enums.UserRole;
import com.tpa.helper.EmailService;
import com.tpa.kafka.producer.ClaimEventPipelineProducer;
import com.tpa.kafka.event.ClaimLifecycleEvent;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Intelligent Claim Assignment and SLA Escalation Engine.
 *
 * Assignment rules (priority order):
 * 1. Amount > ₹5,00,000 → Assign to SENIOR_OFFICER (highest priority)
 * 2. Oncology/Cardiac ICD codes → Assign to available MEDICAL_OFFICER (specialist)
 * 3. Maternity / OB codes → Assign to SPECIALIST
 * 4. Normal → Round-robin among FMG_EMPLOYEE agents
 *
 * SLA Escalation (scheduled every 30 min):
 * - RED ZONE: < 4 hours to breach → fire alert event + email
 * - BREACH: Past deadline → escalate to senior + flag in DB
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimAssignmentService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ClaimEventPipelineProducer pipelineProducer;

    // ICD codes that require specialist review
    private static final Set<String> CARDIAC_CODES = Set.of(
            "I21.9", "I50.9", "I63.9", "I20.9", "I25.10", "I48.91");
    private static final Set<String> ONCOLOGY_CODES = Set.of(
            "C34.90", "C50.911", "C18.9", "C61", "C80.1", "Z51.11");
    private static final Set<String> MATERNITY_CODES = Set.of(
            "O80", "O82", "O60", "O70", "Z37.0", "Z37.1");

    private static final List<String> TERMINAL_STATUSES = List.of(
            "REJECTED", "SETTLED", "CARRIER_APPROVED");

    private static final double HIGH_VALUE_THRESHOLD = 500_000.0; // ₹5 Lakhs

    // ── Auto-Assign Claim ─────────────────────────────────────────────────────

    @Async("taskExecutor")
    @Transactional
    public void autoAssignClaim(Claim claim) {
        log.info("[ASSIGN] Auto-assigning claim #{} (amount={}, icd={})",
                claim.getId(), claim.getAmount(), claim.getIcdCode());

        String assignedTo;
        String assignmentReason;

        if (claim.getAmount() != null && claim.getAmount() >= HIGH_VALUE_THRESHOLD) {
            // Route to least-loaded SENIOR or ADMIN
            User seniorAgent = findLeastLoadedAgent(UserRole.ADMIN);
            assignedTo = seniorAgent != null ? seniorAgent.getEmail() : "senior_medical_officer";
            assignmentReason = "High-value claim (₹" + String.format("%.0f", claim.getAmount()) + ") → Senior Officer";

        } else if (claim.getIcdCode() != null && isOncologyOrCardiac(claim.getIcdCode())) {
            User specialist = findLeastLoadedAgent(UserRole.FMG_EMPLOYEE);
            assignedTo = specialist != null ? specialist.getEmail() : "specialist_reviewer";
            assignmentReason = "Specialist ICD code (" + claim.getIcdCode() + ") → Medical Specialist";

        } else if (claim.getIcdCode() != null && MATERNITY_CODES.stream()
                .anyMatch(c -> claim.getIcdCode().startsWith(c.split("\\.")[0]))) {
            assignedTo = "maternity_specialist";
            assignmentReason = "Maternity claim → Maternity Specialist";

        } else {
            // Round-robin among employee pool
            User agent = roundRobinEmployee();
            assignedTo = agent != null ? agent.getEmail() : "agent_pool";
            assignmentReason = "Standard claim → Agent pool";
        }

        claim.setAssignedTo(assignedTo);
        claimRepository.save(claim);
        log.info("[ASSIGN] Claim #{} assigned to '{}' — reason: {}", claim.getId(), assignedTo, assignmentReason);
    }

    // ── SLA Escalation Check (every 30 minutes) ────────────────────────────────

    @Scheduled(fixedDelay = 1_800_000)
    @Transactional
    public void runEscalationCheck() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime redZoneThreshold = now.plusHours(4); // 4 hours to breach = RED ZONE
        log.info("[SLA] Running SLA check at {}", now);

        List<Claim> allActive = claimRepository.findAll().stream()
                .filter(c -> c.getSlaDeadline() != null)
                .filter(c -> !TERMINAL_STATUSES.contains(c.getStatus() != null ? c.getStatus().name() : ""))
                .collect(Collectors.toList());

        // RED ZONE: 4 hours or less before breach — alert but don't escalate yet
        List<Claim> redZone = allActive.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getEscalated()))
                .filter(c -> c.getSlaDeadline().isAfter(now) && c.getSlaDeadline().isBefore(redZoneThreshold))
                .collect(Collectors.toList());

        if (!redZone.isEmpty()) {
            log.warn("[SLA] {} claims in RED ZONE (< 4h to breach)", redZone.size());
            redZone.forEach(this::sendRedZoneAlert);
        }

        // BREACHED: Past deadline and not yet escalated
        List<Claim> breached = allActive.stream()
                .filter(c -> Boolean.FALSE.equals(c.getEscalated()))
                .filter(c -> c.getSlaDeadline().isBefore(now))
                .collect(Collectors.toList());

        if (breached.isEmpty()) {
            log.info("[SLA] No SLA breaches found");
        } else {
            log.warn("[SLA] {} breach(es) found — escalating", breached.size());
            breached.forEach(c -> escalateClaim(c, now));
        }
    }

    // ── Red Zone Alert ────────────────────────────────────────────────────────

    private void sendRedZoneAlert(Claim claim) {
        try {
            long hoursLeft = java.time.Duration.between(LocalDateTime.now(), claim.getSlaDeadline()).toHours();
            log.warn("[SLA-REDZONE] Claim #{} SLA breaches in {} hours — status: {}",
                    claim.getId(), hoursLeft, claim.getStatus());

            // Notify assigned agent if set
            if (claim.getAssignedTo() != null && claim.getAssignedTo().contains("@")) {
                emailService.sendSimpleEmail(
                        claim.getAssignedTo(),
                        "🔴 SLA RED ZONE — Claim #" + claim.getId() + " needs attention",
                        "Claim #" + claim.getId() + " (Policy: " + claim.getPolicyNumber() + ") " +
                        "is in the RED ZONE. Only " + hoursLeft + " hours left before SLA breach. " +
                        "Current status: " + claim.getStatus() + ". Please process immediately."
                );
            }
        } catch (Exception e) {
            log.warn("[SLA-REDZONE] Alert failed for claim #{}: {}", claim.getId(), e.getMessage());
        }
    }

    // ── Full Escalation ───────────────────────────────────────────────────────

    private void escalateClaim(Claim claim, LocalDateTime now) {
        try {
            long breachHours = java.time.Duration.between(claim.getSlaDeadline(), now).toHours();
            String reason = String.format("SLA breached by %d hours — auto-escalated to senior reviewer", breachHours);

            claim.setEscalated(true);
            claim.setEscalatedAt(now);
            claim.setEscalationReason(reason);

            // Re-assign to senior admin
            User seniorAdmin = findLeastLoadedAgent(UserRole.ADMIN);
            if (seniorAdmin != null) {
                claim.setAssignedTo(seniorAdmin.getEmail());
            }

            claimRepository.save(claim);

            // Publish escalation event to Kafka
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

            // Notify senior admin
            if (seniorAdmin != null) {
                emailService.sendSimpleEmail(
                        seniorAdmin.getEmail(),
                        "🚨 ESCALATED — Claim #" + claim.getId() + " requires immediate attention",
                        "Claim #" + claim.getId() + " (Policy: " + claim.getPolicyNumber() + ") " +
                        "has been escalated to you. " + reason + ". Please take action immediately."
                );
            }

            log.warn("[SLA] Claim #{} ESCALATED — breach: {}h, assigned to: {}",
                    claim.getId(), breachHours, claim.getAssignedTo());
        } catch (Exception e) {
            log.error("[SLA] Failed to escalate claim #{}: {}", claim.getId(), e.getMessage());
        }
    }

    // ── Assignment Helpers ────────────────────────────────────────────────────

    /**
     * Finds the least-loaded active user with the given role.
     * "Load" = number of claims currently assigned to them.
     */
    private User findLeastLoadedAgent(UserRole role) {
        List<User> agents = userRepository.findByUserRole(role).stream()
                .filter(u -> com.tpa.enums.UserStatus.ACTIVE.equals(u.getUserStatus()))
                .collect(Collectors.toList());

        if (agents.isEmpty()) return null;

        Map<String, Long> loadMap = claimRepository.findAll().stream()
                .filter(c -> c.getAssignedTo() != null)
                .collect(Collectors.groupingBy(Claim::getAssignedTo, Collectors.counting()));

        return agents.stream()
                .min(Comparator.comparingLong(u -> loadMap.getOrDefault(u.getEmail(), 0L)))
                .orElse(agents.get(0));
    }

    /**
     * Round-robin selection among FMG_EMPLOYEE agents.
     */
    private User roundRobinEmployee() {
        List<User> employees = userRepository.findByUserRole(UserRole.FMG_EMPLOYEE).stream()
                .filter(u -> com.tpa.enums.UserStatus.ACTIVE.equals(u.getUserStatus()))
                .collect(Collectors.toList());
        if (employees.isEmpty()) return null;
        return findLeastLoadedAgent(UserRole.FMG_EMPLOYEE);
    }

    private boolean isOncologyOrCardiac(String icdCode) {
        return ONCOLOGY_CODES.contains(icdCode) || CARDIAC_CODES.contains(icdCode) ||
               icdCode.startsWith("C") || icdCode.startsWith("I2");
    }
}
