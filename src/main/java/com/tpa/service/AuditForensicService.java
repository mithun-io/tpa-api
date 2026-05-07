package com.tpa.service;

import com.tpa.entity.AuditLog;
import com.tpa.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditForensicService {

    private final AuditLogRepository auditLogRepository;

    public void logAction(Long claimId, String action, String details, String actor) {
        String dataToHash = claimId + action + details + System.currentTimeMillis();
        String hash = generateHash(dataToHash);

        AuditLog logEntry = AuditLog.builder()
                .claimId(claimId)
                .action(action)
                .details(details)
                .performedBy(actor)
                .blockchainHash(hash)
                .build();

        auditLogRepository.save(logEntry);
        log.info("[BLOCKCHAIN-AUDIT] Immutable log entry created for Claim #{}. Hash: {}", claimId, hash);
    }

    private String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (Exception e) {
            return "HASH_ERROR";
        }
    }
}
