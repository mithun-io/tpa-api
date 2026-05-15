package com.tpa.service.impl;

import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.claim.ClaimDecisionResponse;
import com.tpa.entity.RuleConfig;
import com.tpa.entity.RuleExecutionAudit;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PolicyStatus;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.repository.RuleConfigRepository;
import com.tpa.repository.RuleExecutionAuditRepository;
import com.tpa.service.RuleEngineService;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEngineServiceImpl implements RuleEngineService {

    private final KieContainer kieContainer;

    private final RuleConfigRepository ruleConfigRepository;
    private final RuleExecutionAuditRepository ruleExecutionAuditRepository;


    private ClaimDecisionResponse executeDroolsRules(ClaimRequest claimRequest, Long claimId, boolean simulationMode) {
        ClaimDecisionResponse claimDecisionResponse = initializeDecisionResponse();

        KieSession kieSession = kieContainer.newKieSession();

        try {
            kieSession.setGlobal("decision", claimDecisionResponse);
            kieSession.insert(claimRequest);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }

        applyDefaultDecision(claimDecisionResponse);
        saveExecutionAudit(
                claimId,
                "DROOLS_RULE_ENGINE",
                "DROOLS",
                1,
                claimDecisionResponse,
                simulationMode,
                true,
                0L
        );
        return claimDecisionResponse;
    }

    private ClaimDecisionResponse executeDynamicRules(ClaimRequest claimRequest, Long claimId, boolean simulationMode, List<RuleConfig> activeRules) {
        ClaimDecisionResponse claimDecisionResponse = initializeDecisionResponse();

        for (RuleConfig ruleConfig : activeRules) {
            long startTime = System.currentTimeMillis();
            boolean fired = false;

            try {
                fired = switch (ruleConfig.getRuleType()) {
                    case "SIMPLE" -> executeSimpleRule(ruleConfig, claimRequest, claimDecisionResponse, simulationMode);
                    case "GROOVY" -> executeGroovyRule(ruleConfig, claimRequest, claimDecisionResponse);
                    default -> false;
                };

            } catch (Exception exception) {
                log.error("Rule execution failed for rule: {}", ruleConfig.getRuleKey(), exception);
                claimDecisionResponse.getReasons().add("Rule execution failed: " + ruleConfig.getRuleKey());
            }

            long executionTime = System.currentTimeMillis() - startTime;

            saveExecutionAudit(claimId, ruleConfig.getRuleKey(), ruleConfig.getRuleType(), ruleConfig.getVersion(), claimDecisionResponse, simulationMode, fired, executionTime);
        }

        applyDefaultDecision(claimDecisionResponse);
        return claimDecisionResponse;
    }

    private ClaimDecisionResponse initializeDecisionResponse() {
        return ClaimDecisionResponse.builder()
                .claimStatus(null)
                .reasons(new ArrayList<>())
                .build();
    }

    private void applyDefaultDecision(ClaimDecisionResponse decisionResponse) {
        if (decisionResponse.getClaimStatus() == null) {
            decisionResponse.setClaimStatus(ClaimStatus.UNDER_REVIEW);
            decisionResponse.getReasons().add("Manual review required");
        }
    }

    private boolean executeSimpleRule(RuleConfig rule, ClaimRequest claimRequest, ClaimDecisionResponse claimDecisionResponse, boolean simulationMode) {
        return switch (rule.getRuleKey()) {
            case "MAX_CLAIM_AMOUNT_AUTO_APPROVE" ->
                    processMaxClaimAmountRule(rule, claimRequest, claimDecisionResponse, simulationMode);
            case "HIGH_AMOUNT_UNDER_REVIEW" ->
                    processHighAmountRule(rule, claimRequest, claimDecisionResponse, simulationMode);
            case "POLICY_INACTIVE_REJECTION" ->
                    processInactivePolicyRule(claimRequest, claimDecisionResponse, simulationMode);
            default -> false;
        };
    }

    private boolean processMaxClaimAmountRule(RuleConfig rule, ClaimRequest claimRequest, ClaimDecisionResponse claimDecisionResponse, boolean simulationMode) {
        double threshold = Double.parseDouble(rule.getRuleValue());

        if (claimRequest.getClaimedAmount() != null && claimRequest.getClaimedAmount() <= threshold) {

            if (!simulationMode) {
                claimDecisionResponse.setClaimStatus(ClaimStatus.AI_VALIDATED);
            }
            claimDecisionResponse.getReasons().add("Claim auto approved by amount threshold");
            return true;
        }
        return false;
    }

    private boolean processHighAmountRule(RuleConfig rule, ClaimRequest claimRequest, ClaimDecisionResponse claimDecisionResponse, boolean simulationMode) {
        double threshold = Double.parseDouble(rule.getRuleValue());

        if (claimRequest.getClaimedAmount() != null && claimRequest.getClaimedAmount() > threshold) {
            if (!simulationMode) {
                claimDecisionResponse.setClaimStatus(ClaimStatus.UNDER_REVIEW);
            }
            claimDecisionResponse.getReasons().add("High amount claim requires manual review");
            return true;
        }

        return false;
    }

    private boolean processInactivePolicyRule(ClaimRequest claimRequest, ClaimDecisionResponse claimDecisionResponse, boolean simulationMode) {
        if (claimRequest.getPolicyStatus() != null && claimRequest.getPolicyStatus() == PolicyStatus.INACTIVE) {
            if (!simulationMode) {
                claimDecisionResponse.setClaimStatus(ClaimStatus.REJECTED);
            }
            claimDecisionResponse.getReasons().add("Policy is inactive");
            return true;
        }

        return false;
    }

    private boolean executeGroovyRule(RuleConfig ruleConfig, ClaimRequest claimRequest, ClaimDecisionResponse claimDecisionResponse) {
        Binding binding = new Binding();

        binding.setVariable("claim", claimRequest);
        binding.setVariable("decision", claimDecisionResponse);

        GroovyShell groovyShell = new GroovyShell(binding);

        Object result = groovyShell.evaluate(ruleConfig.getGroovyScript());

        return Boolean.TRUE.equals(result);
    }

    private void saveExecutionAudit(
            Long claimId,
            String ruleKey,
            String ruleType,
            Integer version,
            ClaimDecisionResponse claimDecisionResponse,
            boolean simulationMode,
            boolean fired,
            long executionTime) {

        RuleExecutionAudit ruleExecutionAudit = RuleExecutionAudit.builder()
                .claimId(claimId)
                .ruleKey(ruleKey)
                .ruleType(ruleType)
                .ruleVersion(version)
                .inputStatus(null)
                .outputStatus(claimDecisionResponse.getClaimStatus())
                .reasons(String.join(", ", claimDecisionResponse.getReasons()))
                .simulation(simulationMode)
                .fired(fired)
                .executionTimeMs(executionTime)
                .executedBy("RULE_ENGINE")
                .build();

        ruleExecutionAuditRepository.save(ruleExecutionAudit);
    }

    @Override
    @Transactional
    public ClaimDecisionResponse evaluateClaim(ClaimRequest claimRequest) {
        return evaluateClaim(claimRequest, null, false);
    }

    @Override
    @Transactional
    public ClaimDecisionResponse evaluateClaim(ClaimRequest claimRequest, Long claimId, boolean simulationMode) {
        List<RuleConfig> activeRules = ruleConfigRepository.findByActiveTrueOrderByPriorityAsc();

        if (activeRules == null || activeRules.isEmpty()) {
            return executeDroolsRules(claimRequest, claimId, simulationMode);
        }

        return executeDynamicRules(claimRequest, claimId, simulationMode, activeRules);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimDecisionResponse simulateClaim(ClaimRequest claimRequest) {
        return evaluateClaim(claimRequest, null, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleConfig> getAllRules() {
        return ruleConfigRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleConfig> getActiveRules() {
        return ruleConfigRepository.findByActiveTrueOrderByPriorityAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public RuleConfig getRule(Long id) {
        return ruleConfigRepository.findById(id).orElseThrow(() -> new NoResourceFoundException("Rule not found"));
    }

    @Override
    @Transactional
    public RuleConfig createRule(RuleConfig ruleConfig, String updatedBy) {
        ruleConfig.setId(null);
        ruleConfig.setVersion(1);
        ruleConfig.setLastUpdatedBy(updatedBy);

        RuleConfig savedRule = ruleConfigRepository.save(ruleConfig);

        log.info("Rule created successfully: {}", savedRule.getRuleKey());
        return savedRule;
    }

    @Override
    @Transactional
    public RuleConfig updateRule(Long id, RuleConfig updatedRule, String updatedBy) {
        RuleConfig ruleConfig = ruleConfigRepository.findById(id).orElseThrow(() -> new NoResourceFoundException("Rule not found"));

        ruleConfig.setRuleKey(updatedRule.getRuleKey());
        ruleConfig.setRuleValue(updatedRule.getRuleValue());
        ruleConfig.setDescription(updatedRule.getDescription());
        ruleConfig.setGroovyScript(updatedRule.getGroovyScript());
        ruleConfig.setRuleType(updatedRule.getRuleType());
        ruleConfig.setPriority(updatedRule.getPriority());
        ruleConfig.setActive(updatedRule.getActive());
        ruleConfig.setSimulationMode(updatedRule.getSimulationMode());
        ruleConfig.setCategory(updatedRule.getCategory());
        ruleConfig.setVersion(ruleConfig.getVersion() + 1);
        ruleConfig.setLastUpdatedBy(updatedBy);

        RuleConfig savedRule = ruleConfigRepository.save(ruleConfig);

        log.info("Rule updated successfully: {}", savedRule.getRuleKey());
        return savedRule;
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        RuleConfig ruleConfig = ruleConfigRepository.findById(id).orElseThrow(() -> new NoResourceFoundException("Rule not found"));

        ruleConfigRepository.delete(ruleConfig);
        log.info("Rule deleted successfully: {}", ruleConfig.getRuleKey());
    }

    @Override
    @Transactional
    public RuleConfig activateRule(Long id, String updatedBy) {
        RuleConfig ruleConfig = ruleConfigRepository.findById(id).orElseThrow(() -> new NoResourceFoundException("Rule not found"));

        ruleConfig.setActive(true);
        ruleConfig.setLastUpdatedBy(updatedBy);

        RuleConfig updatedRule = ruleConfigRepository.save(ruleConfig);

        log.info("Rule activated successfully: {}", updatedRule.getRuleKey());
        return updatedRule;
    }

    @Override
    @Transactional
    public RuleConfig deactivateRule(Long id, String updatedBy) {
        RuleConfig ruleConfig = ruleConfigRepository.findById(id).orElseThrow(() -> new NoResourceFoundException("Rule not found"));

        ruleConfig.setActive(false);
        ruleConfig.setLastUpdatedBy(updatedBy);

        RuleConfig updatedRule = ruleConfigRepository.save(ruleConfig);

        log.info("Rule deactivated successfully: {}", updatedRule.getRuleKey());
        return updatedRule;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionAudit> getClaimAudits(Long claimId) {
        return ruleExecutionAuditRepository.findByClaimIdOrderByExecutedAtDesc(claimId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RuleExecutionAudit> getRuleAudits(String ruleKey, Pageable pageable) {
        return ruleExecutionAuditRepository.findByRuleKeyOrderByExecutedAtDesc(ruleKey, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionAudit> getSimulationAudits() {
        return ruleExecutionAuditRepository.findBySimulationModeTrueOrderByExecutedAtDesc();
    }

    @Override
    @Transactional
    public void seedDefaultRules(String username) {
        if (ruleConfigRepository.count() > 0) {
            return;
        }

        List<RuleConfig> defaultRules = List.of(
                RuleConfig.builder()
                        .ruleKey("MAX_CLAIM_AMOUNT_AUTO_APPROVE")
                        .ruleValue("5000").description("Auto approve low amount claims")
                        .ruleType("SIMPLE")
                        .priority(1)
                        .version(1)
                        .active(true)
                        .simulationMode(false)
                        .category("AMOUNT")
                        .lastUpdatedBy(username)
                        .build(),

                RuleConfig.builder()
                        .ruleKey("HIGH_AMOUNT_UNDER_REVIEW")
                        .ruleValue("50000")
                        .description("High amount claims require manual review")
                        .ruleType("SIMPLE")
                        .priority(2)
                        .version(1)
                        .active(true)
                        .simulationMode(false)
                        .category("AMOUNT")
                        .lastUpdatedBy(username)
                        .build(),

                RuleConfig.builder()
                        .ruleKey("POLICY_INACTIVE_REJECTION")
                        .ruleValue("INACTIVE")
                        .description("Reject claims for inactive policies")
                        .ruleType("SIMPLE")
                        .priority(3)
                        .version(1)
                        .active(true)
                        .simulationMode(false)
                        .category("ELIGIBILITY")
                        .lastUpdatedBy(username)
                        .build(),

                RuleConfig.builder()
                        .ruleKey("HIGH_RISK_FRAUD_ALERT")
                        .ruleValue("70")
                        .description("Flag claims with high fraud risk")
                        .ruleType("SIMPLE")
                        .priority(4)
                        .version(1)
                        .active(true)
                        .simulationMode(true)
                        .category("FRAUD")
                        .lastUpdatedBy(username)
                        .build());

        ruleConfigRepository.saveAll(defaultRules);
        log.info("Default rules seeded successfully");
    }

}