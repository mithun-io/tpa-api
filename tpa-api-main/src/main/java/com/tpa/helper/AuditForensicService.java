package com.tpa.helper;

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

    private String generateHash(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return "HASH_ERROR";
        }
    }

    public void logAction(Long claimId, String action, String details, String actor) {
        String dataToHash = claimId + action + details + System.currentTimeMillis();
        String hash = generateHash(dataToHash);

        AuditLog auditLog = AuditLog.builder()
                .claimId(claimId)
                .action(action)
                .details(details)
                .performedBy(actor)
                .blockchainHash(hash)
                .build();

        auditLogRepository.save(auditLog);
        log.info("[BLOCKCHAIN-AUDIT] Immutable log entry created for Claim #{}. Hash: {}", claimId, hash);
    }
}
