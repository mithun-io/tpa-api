package com.tpa.controller;

import com.tpa.dto.request.payment.CreatePaymentOrderRequest;
import com.tpa.dto.request.payment.VerifyPaymentRequest;
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

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @PostMapping("/create-order")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'FMG_ADMIN')")
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePaymentOrderRequest request) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> orderDetails = paymentService.createOrder(user.getId(), request);
            return ResponseEntity.ok(orderDetails);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to initiate payment: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'FMG_ADMIN')")
    public ResponseEntity<?> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {
        try {
            PaymentResponse paymentResponse = paymentService.verifyPayment(request);
            return ResponseEntity.ok(paymentResponse);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Payment verification failed: " + e.getMessage()));
        }
    }

    @GetMapping("/claim/{claimId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'FMG_ADMIN', 'FMG_CARRIER')")
    public ResponseEntity<PaymentResponse> getPaymentForClaim(@PathVariable Long claimId) {
        PaymentResponse paymentResponse = paymentService.getPaymentByClaimId(claimId);
        return ResponseEntity.ok(paymentResponse);
    }
}
