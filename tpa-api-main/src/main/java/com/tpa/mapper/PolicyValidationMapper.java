package com.tpa.mapper;

import com.tpa.dto.response.claim.CarrierClaimDetailResponse.PolicyInfo;
import com.tpa.dto.response.claim.PolicyStatusResponse;
import com.tpa.entity.Claim;
import com.tpa.helper.PolicyValidationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PolicyValidationMapper {

    private final PolicyValidationHelper policyValidationHelper;

    public PolicyInfo mapPolicyInfo(Claim claim) {

        PolicyStatusResponse policyStatusResponse = policyValidationHelper.buildPolicyStatus(claim);

        return PolicyInfo.builder()
                .policyNumber(policyStatusResponse.getPolicyNumber())
                .policyStatus(policyStatusResponse.getPolicyStatus())
                .reason(policyStatusResponse.getReason())
                .build();
    }
}