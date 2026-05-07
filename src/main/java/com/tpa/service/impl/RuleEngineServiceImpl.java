package com.tpa.service.impl;

import com.tpa.dto.request.ClaimDataRequest;
import com.tpa.dto.response.ClaimDecisionResponse;
import com.tpa.enums.ClaimStatus;
import com.tpa.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleEngineServiceImpl implements RuleEngineService {

    private final KieContainer kieContainer;

    @Override
    public ClaimDecisionResponse evaluateClaim(ClaimDataRequest claimData) {
        ClaimDecisionResponse decision = new ClaimDecisionResponse(null, new ArrayList<>());
        
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.setGlobal("decision", decision);
            kieSession.insert(claimData);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }

        // Default to UNDER_REVIEW if no status set (should not happen with default rule)
        if (decision.getStatus() == null) {
            decision.setStatus(ClaimStatus.UNDER_REVIEW);
        }

        return decision;
    }
}
