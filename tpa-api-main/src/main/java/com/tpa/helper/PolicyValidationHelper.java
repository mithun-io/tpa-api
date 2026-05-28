package com.tpa.helper;

import com.tpa.dto.response.claim.PolicyStatusResponse;
import com.tpa.entity.Claim;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PolicyStatus;
import org.springframework.stereotype.Component;

@Component
public class PolicyValidationHelper {

    public PolicyStatusResponse buildPolicyStatus(Claim claim) {

        boolean hasPolicy = claim.getPolicyNumber() != null && !claim.getPolicyNumber().isBlank() && !claim.getPolicyNumber().startsWith("TEMP-");

        boolean hasAmount = claim.getAmount() != null && claim.getAmount() > 0;

        boolean notRejected = claim.getClaimStatus() != ClaimStatus.REJECTED;

        PolicyStatus policyStatus = (hasPolicy && hasAmount && notRejected) ? PolicyStatus.VALID : PolicyStatus.INVALID;

        String reason = policyStatus == PolicyStatus.VALID
                ? "Policy is active and claim details are complete."
                : !hasPolicy
                  ? "Missing or temporary policy number."
                  : !hasAmount
                    ? "Claim amount is zero or missing."
                    : "Claim has been rejected — policy coverage cannot be applied.";

        return PolicyStatusResponse.builder()
                .claimId(claim.getId())
                .policyNumber(claim.getPolicyNumber())
                .policyStatus(policyStatus)
                .reason(reason)
                .build();
    }
}