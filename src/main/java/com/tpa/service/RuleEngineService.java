package com.tpa.service;

import com.tpa.dto.request.ClaimRequest;
import com.tpa.dto.response.ClaimDecisionResponse;

public interface RuleEngineService {

    /** Evaluate a claim using active DB rules + Drools fallback. */
    ClaimDecisionResponse evaluateClaim(ClaimRequest claimRequest);

    /** Evaluate with explicit claimId for audit logging and optional simulation. */
    ClaimDecisionResponse evaluateClaim(ClaimRequest claimRequest, Long claimId, boolean simulationMode);

    /** Simulate rule evaluation — rules are executed but status changes are NOT persisted. */
    ClaimDecisionResponse simulateClaim(ClaimRequest claimRequest);
}
