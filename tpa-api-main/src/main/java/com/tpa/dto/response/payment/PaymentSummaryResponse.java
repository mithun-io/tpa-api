package com.tpa.dto.response.payment;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryResponse {

    private Double totalSettledAmount;

    private long totalSuccessfulSettlements;

    private long totalFailedPayments;

    private Double successRate;

    private String generatedAt;
}
