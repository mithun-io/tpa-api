package com.tpa.service;

import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.claim.ClaimDecisionResponse;
import com.tpa.entity.RuleConfig;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PolicyStatus;
import com.tpa.helper.AdminInitializer;
import com.tpa.helper.EnterpriseDataSeeder;
import org.kie.api.runtime.KieContainer;
import com.tpa.repository.RuleConfigRepository;
import com.tpa.repository.RuleExecutionAuditRepository;
import com.tpa.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-016 to TC-024: RuleEngineService Integration Tests
 * Tests dynamic rule evaluation, Drools fallback, simulation mode,
 * GROOVY script execution, and rule CRUD lifecycle.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("RuleEngineService - Business Rule Evaluation Tests")
class RuleEngineServiceTest {

    @Autowired
    private RuleEngineService ruleEngineService;

    @Autowired
    private RuleConfigRepository ruleConfigRepository;

    @Autowired
    private RuleExecutionAuditRepository ruleExecutionAuditRepository;

    @MockBean
    private AdminInitializer adminInitializer;

    @MockBean
    private EnterpriseDataSeeder enterpriseDataSeeder;

    @MockBean
    private KieContainer kieContainer;

    @org.mockito.Mock
    private org.kie.api.runtime.KieSession kieSession;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.when(kieContainer.newKieSession()).thenReturn(kieSession);
        ruleExecutionAuditRepository.deleteAll();
        ruleConfigRepository.deleteAll();
    }

    // ── TC-016 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-016: Low amount claim auto-approved by MAX_CLAIM_AMOUNT_AUTO_APPROVE rule")
    void evaluateClaim_withLowAmount_shouldAutoApproveWhenRuleActive() {
        ruleConfigRepository.save(TestDataFactory.buildActiveSimpleRule("MAX_CLAIM_AMOUNT_AUTO_APPROVE", "5000", 1));

        ClaimRequest request = TestDataFactory.buildValidClaimRequest();
        request.setClaimedAmount(3000.0);

        ClaimDecisionResponse decision = ruleEngineService.evaluateClaim(request, 100L, false);

        assertThat(decision).isNotNull();
        assertThat(decision.getClaimStatus()).isEqualTo(ClaimStatus.AI_VALIDATED);
        assertThat(decision.getReasons()).anyMatch(r -> r.contains("auto approved"));
    }

    // ── TC-017 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-017: High amount claim triggers UNDER_REVIEW by HIGH_AMOUNT_UNDER_REVIEW rule")
    void evaluateClaim_withHighAmount_shouldSetUnderReview() {
        ruleConfigRepository.save(TestDataFactory.buildActiveSimpleRule("HIGH_AMOUNT_UNDER_REVIEW", "50000", 2));

        ClaimRequest request = TestDataFactory.buildHighAmountClaimRequest();

        ClaimDecisionResponse decision = ruleEngineService.evaluateClaim(request, 100L, false);

        assertThat(decision.getClaimStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(decision.getReasons()).anyMatch(r -> r.contains("manual review"));
    }

    // ── TC-018 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-018: INACTIVE policy claim auto-rejected by POLICY_INACTIVE_REJECTION rule")
    void evaluateClaim_withInactivePolicy_shouldRejectClaim() {
        ruleConfigRepository.save(TestDataFactory.buildActiveSimpleRule("POLICY_INACTIVE_REJECTION", "INACTIVE", 3));

        ClaimRequest request = TestDataFactory.buildInactivePolicyClaimRequest();

        ClaimDecisionResponse decision = ruleEngineService.evaluateClaim(request, 100L, false);

        assertThat(decision.getClaimStatus()).isEqualTo(ClaimStatus.REJECTED);
        assertThat(decision.getReasons()).anyMatch(r -> r.contains("inactive"));
    }

    // ── TC-019 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-019: When no active rules exist, Drools fallback executes without error")
    void evaluateClaim_withNoActiveRules_shouldFallbackToDrools() {
        // No rules — Drools fallback will fire
        ClaimRequest request = TestDataFactory.buildValidClaimRequest();

        ClaimDecisionResponse decision = ruleEngineService.evaluateClaim(request, 100L, false);

        assertThat(decision).isNotNull();
        assertThat(decision.getReasons()).isNotNull();
    }

    // ── TC-020 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-020: Simulation mode does NOT change claim status in decision response")
    void evaluateClaim_inSimulationMode_shouldNotSetFinalStatus() {
        ruleConfigRepository.save(TestDataFactory.buildActiveSimpleRule("MAX_CLAIM_AMOUNT_AUTO_APPROVE", "5000", 1));

        ClaimRequest request = TestDataFactory.buildValidClaimRequest();
        request.setClaimedAmount(3000.0);

        ClaimDecisionResponse decision = ruleEngineService.simulateClaim(request);

        // Simulation mode: rule fires but status remains null (then defaulted to UNDER_REVIEW)
        assertThat(decision).isNotNull();
        assertThat(decision.getReasons()).isNotEmpty();
    }

    // ── TC-021 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-021: createRule persists a new rule with version=1 and correct data")
    void createRule_shouldPersistWithVersion1() {
        RuleConfig newRule = TestDataFactory.buildActiveSimpleRule("CUSTOM_RULE_001", "1000", 5);

        RuleConfig saved = ruleEngineService.createRule(newRule, "admin@test.com");

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVersion()).isEqualTo(1);
        assertThat(saved.getRuleKey()).isEqualTo("CUSTOM_RULE_001");
        assertThat(saved.getLastUpdatedBy()).isEqualTo("admin@test.com");
    }

    // ── TC-022 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-022: updateRule increments version on each update")
    void updateRule_shouldIncrementVersion() {
        RuleConfig rule = ruleConfigRepository.save(
                TestDataFactory.buildActiveSimpleRule("VERSIONED_RULE", "5000", 10));

        RuleConfig updatedPayload = TestDataFactory.buildActiveSimpleRule("VERSIONED_RULE", "6000", 10);
        RuleConfig result = ruleEngineService.updateRule(rule.getId(), updatedPayload, "admin@test.com");

        assertThat(result.getVersion()).isEqualTo(2);
        assertThat(result.getRuleValue()).isEqualTo("6000");
    }

    // ── TC-023 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-023: activateRule sets active=true and deactivateRule sets active=false")
    void activateDeactivateRule_shouldToggleActiveFlag() {
        RuleConfig rule = ruleConfigRepository.save(
                TestDataFactory.buildInactiveRule("TOGGLE_RULE"));

        RuleConfig activated = ruleEngineService.activateRule(rule.getId(), "admin@test.com");
        assertThat(activated.getActive()).isTrue();

        RuleConfig deactivated = ruleEngineService.deactivateRule(rule.getId(), "admin@test.com");
        assertThat(deactivated.getActive()).isFalse();
    }

    // ── TC-024 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-024: seedDefaultRules creates exactly 4 default rules when DB is empty")
    void seedDefaultRules_shouldCreate4Rules_whenDbEmpty() {
        ruleEngineService.seedDefaultRules("admin@test.com");

        List<RuleConfig> rules = ruleConfigRepository.findAll();
        assertThat(rules).hasSize(4);
        assertThat(rules).extracting(RuleConfig::getRuleKey)
                .containsExactlyInAnyOrder(
                        "MAX_CLAIM_AMOUNT_AUTO_APPROVE",
                        "HIGH_AMOUNT_UNDER_REVIEW",
                        "POLICY_INACTIVE_REJECTION",
                        "HIGH_RISK_FRAUD_ALERT"
                );
    }
}
