package com.tpa.dto.response.claim;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLeakageResponse {

    private Double totalClaimedAmount;

    private Double totalApprovedPayout;

    private Double leakageAmount;

    private Double leakageRate;

    private long amountMismatchCount;

    private String generatedAt;
}
