package com.tpa.controller;

import com.tpa.dto.request.ClaimDataRequest;
import com.tpa.dto.response.claim.ClaimDecisionResponse;
import com.tpa.entity.RuleConfig;
import com.tpa.entity.RuleExecutionAudit;
import com.tpa.repository.RuleConfigRepository;
import com.tpa.repository.RuleExecutionAuditRepository;
import com.tpa.service.RuleEngineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FMG_ADMIN')")
public class RuleController {

    private final RuleConfigRepository ruleConfigRepository;
    private final RuleExecutionAuditRepository auditRepository;
    private final RuleEngineService ruleEngineService;

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<RuleConfig>> getAllRules() {
        return ResponseEntity.ok(ruleConfigRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<RuleConfig>> getActiveRules() {
        return ResponseEntity.ok(ruleConfigRepository.findByActiveTrueOrderByPriorityAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RuleConfig> getRule(@PathVariable Long id) {
        return ruleConfigRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RuleConfig> createRule(@Valid @RequestBody RuleConfig rule, Principal principal) {
        if (ruleConfigRepository.existsByRuleKey(rule.getRuleKey())) {
            return ResponseEntity.badRequest().build();
        }
        rule.setLastUpdatedBy(principal.getName());
        rule.setVersion(1);
        return ResponseEntity.ok(ruleConfigRepository.save(rule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RuleConfig> updateRule(@PathVariable Long id, @RequestBody RuleConfig updated, Principal principal) {
        return ruleConfigRepository.findById(id).map(existing -> {
            existing.setRuleValue(updated.getRuleValue());
            existing.setDescription(updated.getDescription());
            existing.setGroovyScript(updated.getGroovyScript());
            existing.setRuleType(updated.getRuleType());
            existing.setPriority(updated.getPriority());
            existing.setCategory(updated.getCategory());
            existing.setSimulationMode(updated.getSimulationMode());
            existing.setVersion(existing.getVersion() + 1);
            existing.setLastUpdatedBy(principal.getName());
            return ResponseEntity.ok(ruleConfigRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        if (!ruleConfigRepository.existsById(id)) return ResponseEntity.notFound().build();
        ruleConfigRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── Activate / Deactivate ─────────────────────────────────────────────────

    @PatchMapping("/{id}/activate")
    public ResponseEntity<RuleConfig> activateRule(@PathVariable Long id, Principal principal) {
        return ruleConfigRepository.findById(id).map(rule -> {
            rule.setActive(true);
            rule.setLastUpdatedBy(principal.getName());
            rule.setVersion(rule.getVersion() + 1);
            log.info("Rule '{}' activated by {}", rule.getRuleKey(), principal.getName());
            return ResponseEntity.ok(ruleConfigRepository.save(rule));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<RuleConfig> deactivateRule(@PathVariable Long id, Principal principal) {
        return ruleConfigRepository.findById(id).map(rule -> {
            rule.setActive(false);
            rule.setLastUpdatedBy(principal.getName());
            rule.setVersion(rule.getVersion() + 1);
            log.info("Rule '{}' deactivated by {}", rule.getRuleKey(), principal.getName());
            return ResponseEntity.ok(ruleConfigRepository.save(rule));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Simulation ────────────────────────────────────────────────────────────

    /**
     * Simulate rule evaluation against a mock claim — does NOT persist any status changes.
     */
    @PostMapping("/simulate")
    public ResponseEntity<ClaimDecisionResponse> simulate(@RequestBody ClaimDataRequest claimDataRequest) {
        log.info("[RULE-SIM] Running simulation for policy: {}", claimDataRequest.getPolicyNumber());
        ClaimDecisionResponse result = ruleEngineService.simulateClaim(claimDataRequest);
        return ResponseEntity.ok(result);
    }

    // ── Audit Logs ────────────────────────────────────────────────────────────

    @GetMapping("/audits/claim/{claimId}")
    public ResponseEntity<List<RuleExecutionAudit>> getAuditsByClaimId(@PathVariable Long claimId) {
        return ResponseEntity.ok(auditRepository.findByClaimIdOrderByExecutedAtDesc(claimId));
    }

    @GetMapping("/audits/rule/{ruleKey}")
    public ResponseEntity<Page<RuleExecutionAudit>> getAuditsByRule(@PathVariable String ruleKey, Pageable pageable) {
        return ResponseEntity.ok(auditRepository.findByRuleKeyOrderByExecutedAtDesc(ruleKey, pageable));
    }

    @GetMapping("/audits/simulations")
    public ResponseEntity<List<RuleExecutionAudit>> getSimulationAudits() {
        return ResponseEntity.ok(auditRepository.findBySimulationTrueOrderByExecutedAtDesc());
    }

    // ── Seed Default Rules ────────────────────────────────────────────────────

    @PostMapping("/seed")
    public ResponseEntity<Map<String, String>> seedDefaultRules(Principal principal) {
        seedIfAbsent("HIGH_AMOUNT_UNDER_REVIEW", "100000", "SIMPLE", "AMOUNT",
                "Claims above $100,000 are sent for manual review", 10, principal.getName());
        seedIfAbsent("MAX_CLAIM_AMOUNT_AUTO_APPROVE", "5000", "SIMPLE", "ELIGIBILITY",
                "Claims at or below $5,000 are auto-validated", 20, principal.getName());
        seedIfAbsent("MIN_CLAIM_AMOUNT_AUTO_REJECT", "1", "SIMPLE", "ELIGIBILITY",
                "Claims below $1 are auto-rejected", 5, principal.getName());
        seedIfAbsent("MISSING_DISCHARGE_DATE_FLAG", "true", "SIMPLE", "MEDICAL",
                "Claims without discharge date go under review", 30, principal.getName());

        // Sample Groovy rule
        seedGroovyIfAbsent("GROOVY_HIGH_RISK_DUAL_CONDITION",
                "FRAUD",
                "Flag claims with high amount AND missing diagnosis for manual review",
                50,
                principal.getName(),
                """
                if (claim.claimedAmount != null && claim.claimedAmount > 50000 && (claim.claimFormDiagnosis == null || claim.claimFormDiagnosis?.trim()?.isEmpty())) {
                    decision.reasons.add("HIGH RISK: Large claim with no diagnosis — manual review required");
                    if (!decision.status?.name()?.equals("REJECTED")) {
                        decision.status = com.tpa.enums.ClaimStatus.UNDER_REVIEW;
                    }
                    return true;
                }
                return false;
                """);

        return ResponseEntity.ok(Map.of("message", "Default rules seeded successfully"));
    }

    private void seedIfAbsent(String key, String value, String type, String category,
                               String desc, int priority, String user) {
        if (!ruleConfigRepository.existsByRuleKey(key)) {
            ruleConfigRepository.save(RuleConfig.builder()
                    .ruleKey(key).ruleValue(value).ruleType(type).category(category)
                    .description(desc).priority(priority).active(true)
                    .simulationMode(false).version(1).lastUpdatedBy(user).build());
            log.info("[RULE-SEED] Created rule: {}", key);
        }
    }

    private void seedGroovyIfAbsent(String key, String category, String desc,
                                     int priority, String user, String script) {
        if (!ruleConfigRepository.existsByRuleKey(key)) {
            ruleConfigRepository.save(RuleConfig.builder()
                    .ruleKey(key).ruleValue("groovy").ruleType("GROOVY").category(category)
                    .description(desc).groovyScript(script).priority(priority).active(true)
                    .simulationMode(false).version(1).lastUpdatedBy(user).build());
            log.info("[RULE-SEED] Created Groovy rule: {}", key);
        }
    }
}
