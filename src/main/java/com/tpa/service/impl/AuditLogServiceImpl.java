package com.tpa.service.impl;

import com.tpa.entity.AuditLog;
import com.tpa.entity.ClaimAudit;
import com.tpa.entity.ClaimStatusTimeline;
import com.tpa.enums.ClaimStatus;
import com.tpa.repository.AuditLogRepository;
import com.tpa.repository.ClaimAuditRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.ClaimStatusTimelineRepository;
import com.tpa.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

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

    @Override
    @Async("taskExecutor")
    public void logAction(Long claimId, String action, ClaimStatus previousStatus, ClaimStatus newStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = (authentication != null && authentication.getName() != null)
                ? authentication.getName() : "SYSTEM";

        // Fetch the hash of the last audit record for this claim (chain link)
        String previousHash = auditLogRepository.findTopByClaimIdOrderByIdDesc(claimId)
                .map(AuditLog::getIntegrityHash)
                .orElse("GENESIS");

        // Build the payload string for hashing
        String prevStatusStr = previousStatus != null ? previousStatus.name() : "NULL";
        String newStatusStr  = newStatus != null ? newStatus.name() : "NULL";
        String payload = claimId + "|" + action + "|" + prevStatusStr + "|" + newStatusStr + "|" + performedBy + "|" + previousHash;
        String hash = computeSha256(payload);

        AuditLog auditLog = AuditLog.builder()
                .claimId(claimId)
                .action(action)
                .previousStatus(prevStatusStr)
                .newStatus(newStatusStr)
                .performedBy(performedBy)
                .integrityHash(hash)
                .previousHash(previousHash)
                .blockchainHash(hash) // backward compat
                .build();
        auditLogRepository.save(auditLog);

        // Record status timeline for real-time tracking
        if (previousStatus != null || newStatus != null) {
            ClaimStatusTimeline timeline = ClaimStatusTimeline.builder()
                    .claimId(claimId)
                    .fromStatus(prevStatusStr)
                    .toStatus(newStatusStr)
                    .notes(action.replace("_", " "))
                    .changedBy(performedBy)
                    .build();
            timelineRepository.save(timeline);
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
                log.error("[AUDIT-INTEGRITY] Chain broken at record {} for claim {} — expected prev hash '{}' but got '{}'",
                        record.getId(), claimId, expectedPrevious, record.getPreviousHash());
                return false;
            }

            // Recompute hash and verify
            String prevStatus = record.getPreviousStatus() != null ? record.getPreviousStatus() : "NULL";
            String newStatus  = record.getNewStatus() != null ? record.getNewStatus() : "NULL";
            String payload = claimId + "|" + record.getAction() + "|" + prevStatus + "|" + newStatus
                    + "|" + record.getPerformedBy() + "|" + record.getPreviousHash();
            String recomputed = computeSha256(payload);

            if (!recomputed.equals(record.getIntegrityHash())) {
                log.error("[AUDIT-INTEGRITY] Hash mismatch at record {} for claim {} — possible TAMPERING DETECTED",
                        record.getId(), claimId);
                return false;
            }
            expectedPrevious = record.getIntegrityHash();
        }

        log.info("[AUDIT-INTEGRITY] Chain verified for claim {} — {} records intact", claimId, records.size());
        return true;
    }

    private String computeSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            log.error("[AUDIT] SHA-256 computation failed: {}", e.getMessage());
            return "HASH_ERROR";
        }
    }
}
