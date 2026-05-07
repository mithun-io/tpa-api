package com.tpa.dto.response;

import com.tpa.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudClaimDto {

    private Long claimId;

    private String policyNumber;

    private Double amount;

    private Double riskScore;

    private RiskLevel riskLevel;

    private List<String> reasons;
}
