package com.tpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable audit log with SHA-256 hash chaining.
 * Each record contains:
 * - integrityHash: SHA-256 of this record's payload
 * - previousHash: integrityHash of the previous record for the same claimId
 *
 * This creates a blockchain-style chain — tampering with any record
 * breaks the chain and is detectable via the /api/v1/audit/verify endpoint.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_al_claim_id", columnList = "claimId"),
        @Index(name = "idx_al_timestamp", columnList = "timestamp"),
        @Index(name = "idx_al_action", columnList = "action")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long claimId;

    @Column(nullable = false)
    private String action;

    private String previousStatus;

    private String newStatus;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String performedBy;

    @Column(columnDefinition = "TEXT")
    private String details;

    /**
     * SHA-256 hash of: claimId + action + previousStatus + newStatus + timestamp + performedBy
     */
    @Column(length = 64)
    private String integrityHash;

    /**
     * SHA-256 hash of the previous audit record for this claimId (blockchain chain link).
     * "GENESIS" for the first record.
     */
    @Column(length = 64)
    private String previousHash;

    /**
     * @deprecated Kept for backward compatibility. Use integrityHash instead.
     */
    @Deprecated
    private String blockchainHash;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
