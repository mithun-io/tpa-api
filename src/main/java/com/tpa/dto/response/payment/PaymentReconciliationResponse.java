package com.tpa.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReconciliationResponse {

    private long totalVerifiedPayments;

    private Double totalAmountSettled;

    private String currency;

    private String reconciledAt;
}