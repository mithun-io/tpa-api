package com.tpa.service.impl;

import com.tpa.dto.response.payment.PaymentReconciliationResponse;
import com.tpa.dto.response.auth.VerifyAuditResponse;
import com.tpa.entity.*;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PaymentEventType;
import com.tpa.repository.*;
import com.tpa.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

/**
 * Enhanced AuditLogService with:
 * - SHA-256 hash integrity per log entry
 * - Blockchain-style hash chaining (each record links to previous)
 * - Status timeline recording for WebSocket tracking
 * - ClaimAudit recording (existing behavior preserved)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ClaimAuditRepository claimAuditRepository;
    private final ClaimRepository claimRepository;
    private final ClaimStatusTimelineRepository timelineRepository;
    private final EventAuditLogRepository eventAuditLogRepository;
    private final PaymentLedgerRepository paymentLedgerRepository;

    private String computeSha256(String input) {
        try {
            MessageDigest messageDigestst = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigestst.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);

        } catch (Exception e) {
            log.error("[AUDIT] SHA-256 computation failed: {}", e.getMessage());
            return "HASH_ERROR";
        }
    }

    @Override
    @Async("taskExecutor")
    public void logAction(Long claimId, String action, ClaimStatus previousStatus, ClaimStatus newStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String performedBy = (authentication != null && authentication.getName() != null) ? authentication.getName() : "SYSTEM";
        
        String ipAddress = "UNKNOWN";
        try {
            org.springframework.web.context.request.ServletRequestAttributes attributes = 
                (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                ipAddress = attributes.getRequest().getRemoteAddr();
            }
        } catch (Exception e) {}

        // Fetch the hash of the last audit record for this claim (chain link)
        String previousHash = auditLogRepository.findTopByClaimIdOrderByIdDesc(claimId)
                .map(AuditLog::getIntegrityHash)
                .orElse("GENESIS");

        // Build the payload string for hashing
        String prevStatusStr = previousStatus != null ? previousStatus.name() : "UNKNOWN";

        String newStatusStr = newStatus != null ? newStatus.name() : "UNKNOWN";

        String payload = claimId +
                "|" + action +
                "|" + prevStatusStr +
                "|" + newStatusStr +
                "|" + performedBy +
                "|" + previousHash + 
                "|" + ipAddress;

        String hash = computeSha256(payload);

        AuditLog auditLog = AuditLog.builder()
                .claimId(claimId)
                .action(action)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .performedBy(performedBy)
                .ipAddress(ipAddress)
                .integrityHash(hash)
                .previousHash(previousHash)
                .blockchainHash(hash)
                .build();

        auditLogRepository.save(auditLog);

        // Record status timeline for real-time tracking
        if (previousStatus != null || newStatus != null) {
            ClaimStatusTimeline claimStatusTimeline = ClaimStatusTimeline.builder()
                    .claimId(claimId)
                    .fromStatus(prevStatusStr)
                    .toStatus(newStatusStr)
                    .notes(action.replace("_", " "))
                    .changedBy(performedBy)
                    .build();
            timelineRepository.save(claimStatusTimeline);
        }

        // Record ClaimAudit (existing behavior)
        if (newStatus != null) {
            claimRepository.findById(claimId).ifPresent(claim -> {
                ClaimAudit claimAudit = ClaimAudit.builder()
                        .claim(claim)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .changedBy(performedBy)
                        .notes(action.replace("_", " "))
                        .build();
                claimAuditRepository.save(claimAudit);
            });
        }

        log.debug("[AUDIT] Logged action '{}' for claim {} — hash: {}", action, claimId, hash.substring(0, 8) + "...");
    }

    /**
     * Verifies the integrity chain for all audit records of a given claim.
     * Returns true if the chain is intact (no tampering detected).
     */
    public boolean verifyAuditChain(Long claimId) {
        var records = auditLogRepository.findByClaimIdOrderByIdAsc(claimId);
        if (records.isEmpty()) return true;

        String expectedPrevious = "GENESIS";
        for (AuditLog record : records) {
            // Verify previousHash matches the chain
            if (!expectedPrevious.equals(record.getPreviousHash())) {
                log.error("[AUDIT-INTEGRITY] Chain broken at record {} for claim {} — expected prev hash '{}' but got '{}'", record.getId(), claimId, expectedPrevious, record.getPreviousHash());
                return false;
            }

            // Recompute hash and verify
            String prevStatus = record.getPreviousStatus() != null ? record.getPreviousStatus().name() : "UNKNOWN";
            String newStatus = record.getNewStatus() != null ? record.getNewStatus().name() : "UNKNOWN";

            String payload = claimId +
                    "|" + record.getAction() +
                    "|" + prevStatus +
                    "|" + newStatus +
                    "|" + record.getPerformedBy() +
                    "|" + record.getPreviousHash();
            
            if (record.getIpAddress() != null) {
                payload += "|" + record.getIpAddress();
            }

            String recomputed = computeSha256(payload);
            if (!recomputed.equals(record.getIntegrityHash())) {
                log.error("[AUDIT-INTEGRITY] Hash mismatch at record {} for claim {} — possible TAMPERING DETECTED", record.getId(), claimId);
                return false;
            }

            expectedPrevious = record.getIntegrityHash();
        }

        log.info("[AUDIT-INTEGRITY] Chain verified for claim {} — {} records intact", claimId, records.size());
        return true;
    }

    @Override
    public List<AuditLog> getClaimAuditTrail(Long claimId) {
        return auditLogRepository.findByClaimIdOrderByIdAsc(claimId);
    }

    @Override
    public List<AuditLog> getByClaimAndAction(Long claimId, String action) {
        return auditLogRepository.findByClaimIdAndAction(claimId, action);
    }

    @Override
    public List<AuditLog> getAuditsByTimeRange(LocalDateTime from, LocalDateTime to) {
        return auditLogRepository.findByTimestampBetween(from, to);
    }

    @Override
    public VerifyAuditResponse verifyIntegrity(Long claimId) {
        boolean isIntact = verifyAuditChain(claimId);

        List<AuditLog> records = auditLogRepository.findByClaimIdOrderByIdAsc(claimId);

        return VerifyAuditResponse.builder()
                .claimId(claimId)
                .chainIntact(isIntact)
                .totalRecords(records.size())
                .verifiedAt(LocalDateTime.now().toString())
                .message(isIntact ? "Audit chain is intact" : "Integrity violation detected")
                .build();
    }

    @Override
    public List<EventAuditLog> getEventsByClaimId(Long claimId) {
        return eventAuditLogRepository.findByClaimIdOrderByReceivedAtDesc(claimId);
    }

    @Override
    public List<EventAuditLog> getEventsByStage(String stage) {
        return eventAuditLogRepository.findByStageOrderByReceivedAtDesc(stage);
    }

    @Override
    public List<EventAuditLog> getUnprocessedEvents() {
        return eventAuditLogRepository.findByProcessedFalseOrderByReceivedAtAsc();
    }

    @Override
    public List<PaymentLedger> getPaymentLedger(Long claimId) {
        return paymentLedgerRepository.findByClaimIdOrderByCreatedAtAsc(claimId);
    }

    @Override
    public List<PaymentLedger> getPaymentsByPaymentId(Long paymentId) {
        return paymentLedgerRepository.findByPaymentIdOrderByCreatedAtAsc(paymentId);
    }

    @Override
    public List<PaymentLedger> getPaymentsByPaymentEventType(PaymentEventType paymentEventType) {
        return paymentLedgerRepository.findByPaymentEventTypeOrderByCreatedAtDesc(paymentEventType);
    }

    @Override
    public PaymentReconciliationResponse reconcilePayments() {
        Double total = paymentLedgerRepository.sumVerifiedPayments();

        long totalPaid = paymentLedgerRepository.countByPaymentEventType(PaymentEventType.PAYMENT_SUCCESS);

        return PaymentReconciliationResponse.builder()
                .totalVerifiedPayments(totalPaid)
                .totalAmountSettled(total != null ? total : 0.0)
                .currency("INR")
                .reconciledAt(LocalDateTime.now().toString())
                .build();
    }

}
