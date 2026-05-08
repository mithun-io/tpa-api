package com.tpa.controller;

import com.tpa.entity.AuditLog;
import com.tpa.entity.EventAuditLog;
import com.tpa.entity.PaymentLedger;
import com.tpa.repository.AuditLogRepository;
import com.tpa.repository.EventAuditLogRepository;
import com.tpa.repository.PaymentLedgerRepository;
import com.tpa.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Forensic audit API for compliance and integrity verification.
 * Provides:
 * - Claim audit trail (action log)
 * - SHA-256 chain integrity verification
 * - Kafka event audit queries
 * - Payment ledger queries
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FMG_ADMIN')")
public class AuditController {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;
    private final EventAuditLogRepository eventAuditLogRepository;
    private final PaymentLedgerRepository paymentLedgerRepository;

    // ── Claim Audit Trail ─────────────────────────────────────────────────────

    @GetMapping("/claims/{claimId}")
    public ResponseEntity<List<AuditLog>> getClaimAuditTrail(@PathVariable Long claimId) {
        return ResponseEntity.ok(auditLogRepository.findByClaimIdOrderByIdAsc(claimId));
    }

    @GetMapping("/claims/{claimId}/action/{action}")
    public ResponseEntity<List<AuditLog>> getByClaimAndAction(@PathVariable Long claimId, @PathVariable String action) {
        return ResponseEntity.ok(auditLogRepository.findByClaimIdAndAction(claimId, action));
    }

    @GetMapping("/range")
    public ResponseEntity<List<AuditLog>> getAuditsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(auditLogRepository.findByTimestampBetween(from, to));
    }

    // ── Integrity Verification (SHA-256 Chain) ─────────────────────────────────

    @GetMapping("/claims/{claimId}/verify")
    public ResponseEntity<Map<String, Object>> verifyIntegrity(@PathVariable Long claimId) {
        log.info("[AUDIT-VERIFY] Verifying audit chain for claim {}", claimId);
        boolean isIntact = auditLogService.verifyAuditChain(claimId);
        List<AuditLog> records = auditLogRepository.findByClaimIdOrderByIdAsc(claimId);

        return ResponseEntity.ok(Map.of(
                "claimId", claimId,
                "chainIntact", isIntact,
                "totalRecords", records.size(),
                "verifiedAt", LocalDateTime.now().toString(),
                "message", isIntact
                        ? "✅ Audit chain is INTACT — no tampering detected"
                        : "🚨 INTEGRITY VIOLATION DETECTED — audit chain is broken"
        ));
    }

    // ── Kafka Event Audit ────────────────────────────────────────────────────

    @GetMapping("/events/claim/{claimId}")
    public ResponseEntity<List<EventAuditLog>> getEventsByClaimId(@PathVariable Long claimId) {
        return ResponseEntity.ok(eventAuditLogRepository.findByClaimIdOrderByReceivedAtDesc(claimId));
    }

    @GetMapping("/events/stage/{stage}")
    public ResponseEntity<List<EventAuditLog>> getEventsByStage(@PathVariable String stage) {
        return ResponseEntity.ok(eventAuditLogRepository.findByStageOrderByReceivedAtDesc(stage));
    }

    @GetMapping("/events/unprocessed")
    public ResponseEntity<List<EventAuditLog>> getUnprocessedEvents() {
        return ResponseEntity.ok(eventAuditLogRepository.findByProcessedFalseOrderByReceivedAtAsc());
    }

    // ── Payment Ledger ────────────────────────────────────────────────────────

    @GetMapping("/payments/claim/{claimId}")
    public ResponseEntity<List<PaymentLedger>> getPaymentLedger(@PathVariable Long claimId) {
        return ResponseEntity.ok(paymentLedgerRepository.findByClaimIdOrderByCreatedAtAsc(claimId));
    }

    @GetMapping("/payments/reconcile")
    public ResponseEntity<Map<String, Object>> reconcilePayments() {
        Double total = paymentLedgerRepository.sumVerifiedPayments();
        long totalPaid = paymentLedgerRepository.findByEventTypeOrderByCreatedAtDesc("PAYMENT_VERIFIED").size();
        return ResponseEntity.ok(Map.of(
                "totalVerifiedPayments", totalPaid,
                "totalAmountSettled", total != null ? total : 0.0,
                "currency", "INR",
                "reconciledAt", LocalDateTime.now().toString()
        ));
    }
}
