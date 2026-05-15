package com.tpa.dto.response.analytics;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LossRatioResponse {

    private Double totalClaimsPaid;

    private Double estimatedPremiumPool;

    private Double lossRatioPercent;

    private String lossRatioStatus;

    private long settledClaims;

    private long rejectedClaims;

    private long totalClaims;

    private String generatedAt;
}
