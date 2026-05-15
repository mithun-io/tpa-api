package com.tpa.controller;

import com.tpa.dto.request.payment.CreatePaymentOrderRequest;
import com.tpa.dto.request.payment.VerifyPaymentRequest;
import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.payment.PaymentOrderResponse;
import com.tpa.dto.response.payment.PaymentResponse;
import com.tpa.entity.User;
import com.tpa.repository.UserRepository;
import com.tpa.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private final UserRepository userRepository;

    @PostMapping("/create-order")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> createOrder(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CreatePaymentOrderRequest createPaymentOrderRequest) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment order created successfully", paymentService.createOrder(user.getId(), createPaymentOrderRequest), 200));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(@Valid @RequestBody VerifyPaymentRequest verifyPaymentRequest) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment verified successfully", paymentService.verifyPayment(verifyPaymentRequest), 200));
    }

    @GetMapping("/claim/{claimId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN', CARRIER)")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByClaimId(@PathVariable Long claimId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment details fetched successfully", paymentService.getPaymentByClaimId(claimId), 200));
    }
}