package com.tpa.service;

import com.tpa.dto.request.payment.CreatePaymentOrderRequest;
import com.tpa.dto.request.payment.VerifyPaymentRequest;
import com.tpa.dto.response.payment.PaymentOrderResponse;
import com.tpa.dto.response.payment.PaymentResponse;
import com.tpa.entity.Claim;

public interface PaymentService {

    PaymentOrderResponse createOrder(Long userId, CreatePaymentOrderRequest request);

    PaymentResponse verifyPayment(VerifyPaymentRequest request);

    PaymentResponse getPaymentByClaimId(Long claimId);

    void initiateInstantPayout(Claim claim);
}