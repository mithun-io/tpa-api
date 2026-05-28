package com.tpa.controller;

import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.payment.PaymentReconciliationResponse;
import com.tpa.dto.response.auth.VerifyAuditResponse;
import com.tpa.entity.AuditLog;
import com.tpa.entity.EventAuditLog;
import com.tpa.entity.PaymentLedger;
import com.tpa.enums.PaymentEventType;
import com.tpa.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Forensic audit API for compliance and integrity verification.
 * Provides:
 * - Claim audit trail (action log)
 * - SHA-256 chain integrity verification
 * - Kafka event audit queries
 * - Payment ledger queries
 */

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/claims/{claimId}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getClaimAuditTrail(@PathVariable Long claimId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim audit trail fetched successfully", auditLogService.getClaimAuditTrail(claimId), 200));
    }

    @GetMapping("/claims/{claimId}/action/{action}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getByClaimAndAction(@PathVariable Long claimId, @PathVariable String action) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Audit logs fetched successfully", auditLogService.getByClaimAndAction(claimId, action), 200));
    }

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditsByTimeRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Audit logs fetched successfully", auditLogService.getAuditsByTimeRange(from, to), 200));
    }

    @GetMapping("/claims/{claimId}/verify")
    public ResponseEntity<ApiResponse<VerifyAuditResponse>> verifyIntegrity(@PathVariable Long claimId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Audit integrity verified successfully", auditLogService.verifyIntegrity(claimId), 200));
    }

    @GetMapping("/events/claim/{claimId}")
    public ResponseEntity<ApiResponse<List<EventAuditLog>>> getEventsByClaimId(@PathVariable Long claimId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Event audit logs fetched successfully", auditLogService.getEventsByClaimId(claimId), 200));
    }

    @GetMapping("/events/stage/{stage}")
    public ResponseEntity<ApiResponse<List<EventAuditLog>>> getEventsByStage(@PathVariable String stage) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Event audit logs fetched successfully", auditLogService.getEventsByStage(stage), 200));
    }

    @GetMapping("/events/unprocessed")
    public ResponseEntity<ApiResponse<List<EventAuditLog>>> getUnprocessedEvents() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Unprocessed events fetched successfully", auditLogService.getUnprocessedEvents(), 200));
    }

    @GetMapping("/payments/claim/{claimId}")
    public ResponseEntity<ApiResponse<List<PaymentLedger>>> getPaymentLedger(@PathVariable Long claimId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment ledger fetched successfully", auditLogService.getPaymentLedger(claimId), 200));
    }

    @GetMapping("/payments/payment/{paymentId}")
    public ResponseEntity<ApiResponse<List<PaymentLedger>>> getPaymentsByPaymentId(@PathVariable Long paymentId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments fetched successfully", auditLogService.getPaymentsByPaymentId(paymentId), 200));
    }

    @GetMapping("/payments/event/{eventType}")
    public ResponseEntity<ApiResponse<List<PaymentLedger>>> getPaymentsByEventType(@PathVariable PaymentEventType eventType) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments fetched successfully", auditLogService.getPaymentsByPaymentEventType(eventType), 200));
    }

    @GetMapping("/payments/reconcile")
    public ResponseEntity<ApiResponse<PaymentReconciliationResponse>> reconcilePayments() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments reconciled successfully", auditLogService.reconcilePayments(), 200));
    }
}