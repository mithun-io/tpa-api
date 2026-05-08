package com.tpa.service;

import com.tpa.dto.request.ClaimDataRequest;
import com.tpa.dto.response.ClaimDecisionResponse;

public interface RuleEngineService {

    /** Evaluate a claim using active DB rules + Drools fallback. */
    ClaimDecisionResponse evaluateClaim(ClaimDataRequest claimData);

    /** Evaluate with explicit claimId for audit logging and optional simulation. */
    ClaimDecisionResponse evaluateClaim(ClaimDataRequest claimData, Long claimId, boolean simulationMode);

    /** Simulate rule evaluation — rules are executed but status changes are NOT persisted. */
    ClaimDecisionResponse simulateClaim(ClaimDataRequest claimData);
}
