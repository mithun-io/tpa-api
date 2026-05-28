package com.tpa.dto.response.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentOrderResponse {

    private String orderId;

    private Integer amount;

    private String currency;

    private String key;

    private Long claimId;
}