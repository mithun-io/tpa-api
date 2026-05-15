package com.tpa.controller;

import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.claim.ClaimDecisionResponse;
import com.tpa.entity.RuleConfig;
import com.tpa.entity.RuleExecutionAudit;
import com.tpa.service.RuleEngineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FMG_ADMIN')")
public class RuleEngineController {

    private final RuleEngineService ruleEngineService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RuleConfig>>> getAllRules() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rules fetched successfully", ruleEngineService.getAllRules(), 200));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RuleConfig>>> getActiveRules() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Active rules fetched successfully", ruleEngineService.getActiveRules(), 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RuleConfig>> getRule(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rule fetched successfully", ruleEngineService.getRule(id), 200));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RuleConfig>> createRule(@Valid @RequestBody RuleConfig ruleConfig, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Rule created successfully", ruleEngineService.createRule(ruleConfig, authentication.getName()), 201));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RuleConfig>> updateRule(@PathVariable Long id, @Valid @RequestBody RuleConfig updatedRule, Authentication authentication) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rule updated successfully", ruleEngineService.updateRule(id, updatedRule, authentication.getName()), 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long id) {
        ruleEngineService.deleteRule(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rule deleted successfully", null, 200));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<RuleConfig>> activateRule(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rule activated successfully", ruleEngineService.activateRule(id, authentication.getName()), 200));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<RuleConfig>> deactivateRule(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rule deactivated successfully", ruleEngineService.deactivateRule(id, authentication.getName()), 200));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<ApiResponse<ClaimDecisionResponse>> evaluateClaim(@Valid @RequestBody ClaimRequest claimRequest) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rule evaluation completed", ruleEngineService.evaluateClaim(claimRequest), 200));
    }

    @PostMapping("/simulate")
    public ResponseEntity<ApiResponse<ClaimDecisionResponse>> simulateClaim(@Valid @RequestBody ClaimRequest claimRequest) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Simulation completed", ruleEngineService.simulateClaim(claimRequest), 200));
    }

    @GetMapping("/audits/claims/{claimId}")
    public ResponseEntity<ApiResponse<List<RuleExecutionAudit>>> getClaimAudits(@PathVariable Long claimId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim audits fetched successfully", ruleEngineService.getClaimAudits(claimId), 200));
    }

    @GetMapping("/audits/rules/{ruleKey}")
    public ResponseEntity<ApiResponse<Page<RuleExecutionAudit>>> getRuleAudits(@PathVariable String ruleKey, Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rule audits fetched successfully", ruleEngineService.getRuleAudits(ruleKey, pageable), 200));
    }

    @GetMapping("/audits/simulations")
    public ResponseEntity<ApiResponse<List<RuleExecutionAudit>>> getSimulationAudits() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Simulation audits fetched successfully", ruleEngineService.getSimulationAudits(), 200));
    }

    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<Void>> seedRules(Authentication authentication) {
        ruleEngineService.seedDefaultRules(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Default rules seeded successfully", null, 200));
    }
}