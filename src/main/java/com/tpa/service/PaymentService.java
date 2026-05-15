package com.tpa.service;

import com.tpa.dto.request.payment.CreatePaymentOrderRequest;
import com.tpa.dto.request.payment.VerifyPaymentRequest;
import com.tpa.dto.response.payment.PaymentResponse;

import java.util.Map;

public interface PaymentService {

    Map<String, Object> createOrder(Long userId, CreatePaymentOrderRequest request);

    PaymentResponse verifyPayment(VerifyPaymentRequest request);

    PaymentResponse getPaymentByClaimId(Long claimId);

    void initiateInstantPayout(com.tpa.entity.Claim claim);
}
