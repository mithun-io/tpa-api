package com.tpa.dto.response.claim;

import com.tpa.enums.PolicyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyStatusResponse {

    private Long claimId;

    private String policyNumber;

    private PolicyStatus policyStatus;

    private String reason;
}
