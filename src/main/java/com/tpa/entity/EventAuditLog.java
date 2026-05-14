package com.tpa.entity;

import com.tpa.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_audit_logs", indexes = {
        @Index(name = "idx_eal_claim_id", columnList = "claimId"),
        @Index(name = "idx_eal_event_id", columnList = "eventId", unique = true),
        @Index(name = "idx_eal_stage", columnList = "stage"),
        @Index(name = "idx_eal_processed", columnList = "processed")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UUID from ClaimLifecycleEvent — used for idempotency checks.
    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private Long claimId;

    @Column(nullable = false)
    private String stage;

    @Column(nullable = false)
    private ClaimStatus claimStatus;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private String topic;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime receivedAt;

    private LocalDateTime processedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean processed = false;

    // Number of retry attempts before successful processing.
    @Column(nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(columnDefinition = "TEXT")
    private String errorDetails;
}
