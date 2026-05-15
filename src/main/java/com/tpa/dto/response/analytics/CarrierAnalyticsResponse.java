package com.tpa.dto.response.analytics;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierAnalyticsResponse {

    private String carrier;

    private int totalClaims;

    private Double totalClaimedAmount;

    private long approvedClaims;

    private long rejectedClaims;

    private Double approvalRate;

    private long highRiskClaims;

    private String generatedAt;
}
