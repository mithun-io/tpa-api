package com.tpa.service;

import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.claim.ClaimDecisionResponse;
import com.tpa.entity.RuleConfig;
import com.tpa.entity.RuleExecutionAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RuleEngineService {

    ClaimDecisionResponse evaluateClaim(ClaimRequest claimRequest);

    ClaimDecisionResponse evaluateClaim(ClaimRequest claimRequest, Long claimId, boolean simulationMode);

    ClaimDecisionResponse simulateClaim(ClaimRequest claimRequest);

    List<RuleConfig> getAllRules();

    List<RuleConfig> getActiveRules();

    RuleConfig getRule(Long id);

    RuleConfig createRule(RuleConfig ruleConfig, String updatedBy);

    RuleConfig updateRule(Long id, RuleConfig updatedRule, String updatedBy);

    void deleteRule(Long id);

    RuleConfig activateRule(Long id, String updatedBy);

    RuleConfig deactivateRule(Long id, String updatedBy);

    List<RuleExecutionAudit> getClaimAudits(Long claimId);

    Page<RuleExecutionAudit> getRuleAudits(String ruleKey, Pageable pageable);

    List<RuleExecutionAudit> getSimulationAudits();

    void seedDefaultRules(String username);
}