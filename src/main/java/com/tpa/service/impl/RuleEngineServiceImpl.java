package com.tpa.service.impl;

import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.claim.ClaimDecisionResponse;
import com.tpa.entity.RuleConfig;
import com.tpa.entity.RuleExecutionAudit;
import com.tpa.enums.ClaimStatus;
import com.tpa.repository.RuleConfigRepository;
import com.tpa.repository.RuleExecutionAuditRepository;
import com.tpa.service.RuleEngineService;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Pluggable Rule Engine that supports:
 * 1. GROOVY rules — dynamic scripts stored in DB, hot-reloadable
 * 2. SIMPLE rules — key/value threshold rules evaluated in Java
 * 3. DROOLS rules — delegates to KieContainer for DRL-based rules
 *
 * Rules are evaluated in ascending priority order. Inactive rules are skipped.
 * Rules in simulationMode are evaluated but do NOT change claim status.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEngineServiceImpl implements RuleEngineService {

    private final KieContainer kieContainer;
    private final RuleConfigRepository ruleConfigRepository;
    private final RuleExecutionAuditRepository auditRepository;

    @Override
    @Transactional
    public ClaimDecisionResponse evaluateClaim(ClaimRequest claimData) {
        return evaluateClaim(claimData, null, false);
    }

    @Override
    @Transactional
    public ClaimDecisionResponse evaluateClaim(ClaimRequest claimData, Long claimId, boolean simulationMode) {
        ClaimDecisionResponse decision = new ClaimDecisionResponse(null, new ArrayList<>());

        List<RuleConfig> activeRules = ruleConfigRepository.findByActiveTrueOrderByPriorityAsc();

        if (activeRules.isEmpty()) {
            log.warn("[BRMS] No active rules found in DB — falling back to Drools KIE session");
            return evaluateWithDrools(claimData, claimId, simulationMode);
        }

        log.info("[BRMS] Evaluating {} active rules for claim {}", activeRules.size(), claimId);

        for (RuleConfig rule : activeRules) {
            long startMs = System.currentTimeMillis();
            boolean fired = false;
            String outputStatus = null;
            String reasonsCapture = null;

            try {
                switch (rule.getRuleType()) {
                    case "GROOVY" -> {
                        fired = evaluateGroovyRule(rule, claimData, decision, simulationMode || Boolean.TRUE.equals(rule.getSimulationMode()));
                    }
                    case "SIMPLE" -> {
                        fired = evaluateSimpleRule(rule, claimData, decision, simulationMode || Boolean.TRUE.equals(rule.getSimulationMode()));
                    }
                    default -> log.warn("[BRMS] Unknown rule type '{}' for rule '{}' — skipping", rule.getRuleType(), rule.getRuleKey());
                }

                outputStatus = decision.getClaimStatus() != null ? decision.getClaimStatus().name() : null;
                reasonsCapture = String.join("; ", decision.getReasons());

            } catch (Exception ex) {
                log.error("[BRMS] Error evaluating rule '{}': {}", rule.getRuleKey(), ex.getMessage(), ex);
                decision.getReasons().add("Rule '" + rule.getRuleKey() + "' failed: " + ex.getMessage());
            }

            long execMs = System.currentTimeMillis() - startMs;

            // Persist rule execution audit
            if (claimId != null) {
                RuleExecutionAudit audit = RuleExecutionAudit.builder()
                        .claimId(claimId)
                        .ruleKey(rule.getRuleKey())
                        .ruleType(rule.getRuleType())
                        .ruleVersion(rule.getVersion())
                        .outputStatus(ClaimStatus.valueOf(outputStatus))
                        .reasons(reasonsCapture)
                        .simulation(simulationMode || Boolean.TRUE.equals(rule.getSimulationMode()))
                        .fired(fired)
                        .executionTimeMs(execMs)
                        .build();
                auditRepository.save(audit);
            }
        }

        // Default to UNDER_REVIEW if no rule set a status
        if (decision.getClaimStatus() == null) {
            decision.setClaimStatus(ClaimStatus.UNDER_REVIEW);
            log.info("[BRMS] No rule set a status — defaulting to UNDER_REVIEW for claim {}", claimId);
        }

        log.info("[BRMS] Final decision for claim {}: status={}, reasons={}", claimId, decision.getClaimStatus(), decision.getReasons());
        return decision;
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Groovy rule evaluator
    // ────────────────────────────────────────────────────────────────────────────

    private boolean evaluateGroovyRule(RuleConfig rule, ClaimRequest claim,
                                        ClaimDecisionResponse decision, boolean simulation) {
        if (rule.getGroovyScript() == null || rule.getGroovyScript().isBlank()) {
            log.warn("[BRMS] Groovy rule '{}' has no script body — skipping", rule.getRuleKey());
            return false;
        }

        Binding binding = new Binding();
        binding.setVariable("claim", claim);
        binding.setVariable("decision", simulation ? copyDecision(decision) : decision);
        binding.setVariable("log", log);

        GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
        Object result = shell.evaluate(rule.getGroovyScript());

        boolean fired = Boolean.TRUE.equals(result);
        if (fired) {
            log.info("[BRMS] Groovy rule '{}' FIRED (simulation={})", rule.getRuleKey(), simulation);
        }
        return fired;
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Simple key/value rule evaluator
    // ────────────────────────────────────────────────────────────────────────────

    private boolean evaluateSimpleRule(RuleConfig rule, ClaimRequest claim,
                                        ClaimDecisionResponse decision, boolean simulation) {
        boolean fired = false;
        String key = rule.getRuleKey();
        String value = rule.getRuleValue();

        switch (key) {
            case "MAX_CLAIM_AMOUNT_AUTO_APPROVE" -> {
                double threshold = Double.parseDouble(value);
                if (claim.getClaimedAmount() != null && claim.getClaimedAmount() <= threshold) {
                    if (!simulation) decision.setClaimStatus(ClaimStatus.AI_VALIDATED);
                    decision.getReasons().add("Auto-validated: amount " + claim.getClaimedAmount() + " ≤ threshold " + threshold);
                    fired = true;
                }
            }
            case "MIN_CLAIM_AMOUNT_AUTO_REJECT" -> {
                double min = Double.parseDouble(value);
                if (claim.getClaimedAmount() != null && claim.getClaimedAmount() < min) {
                    if (!simulation) decision.setClaimStatus(ClaimStatus.REJECTED);
                    decision.getReasons().add("Auto-rejected: amount " + claim.getClaimedAmount() + " below minimum " + min);
                    fired = true;
                }
            }
            case "HIGH_AMOUNT_UNDER_REVIEW" -> {
                double high = Double.parseDouble(value);
                if (claim.getClaimedAmount() != null && claim.getClaimedAmount() > high) {
                    if (!simulation) decision.setClaimStatus(ClaimStatus.UNDER_REVIEW);
                    decision.getReasons().add("Sent for review: high claim amount " + claim.getClaimedAmount() + " > " + high);
                    fired = true;
                }
            }
            case "MISSING_DISCHARGE_DATE_FLAG" -> {
                if (claim.getClaimFormDischargeDate() == null) {
                    if (!simulation) {
                        if (decision.getClaimStatus() == null) decision.setClaimStatus(ClaimStatus.UNDER_REVIEW);
                    }
                    decision.getReasons().add("Missing discharge date — flagged for review");
                    fired = true;
                }
            }
            default -> log.debug("[BRMS] Unknown SIMPLE rule key '{}' — no handler defined", key);
        }

        if (fired) {
            log.info("[BRMS] Simple rule '{}' FIRED (simulation={})", key, simulation);
        }
        return fired;
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Drools fallback
    // ────────────────────────────────────────────────────────────────────────────

    private ClaimDecisionResponse evaluateWithDrools(ClaimRequest claimData, Long claimId, boolean simulation) {
        ClaimDecisionResponse decision = new ClaimDecisionResponse(null, new ArrayList<>());
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.setGlobal("decision", decision);
            kieSession.insert(claimData);
            kieSession.fireAllRules();
            log.info("[BRMS][DROOLS] Rules fired for claim {}", claimId);
        } finally {
            kieSession.dispose();
        }
        if (decision.getClaimStatus() == null) {
            decision.setClaimStatus(ClaimStatus.UNDER_REVIEW);
        }
        return decision;
    }

    private ClaimDecisionResponse copyDecision(ClaimDecisionResponse original) {
        return new ClaimDecisionResponse(original.getClaimStatus(), new ArrayList<>(original.getReasons()));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Simulation endpoint support
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ClaimDecisionResponse simulateClaim(ClaimRequest claimData) {
        return evaluateClaim(claimData, null, true);
    }
}
