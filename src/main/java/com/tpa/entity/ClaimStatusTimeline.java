package com.tpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Immutable record of every claim status transition.
 * Used for the customer-facing real-time tracking timeline.
 */
@Entity
@Table(name = "claim_status_timelines", indexes = {
        @Index(name = "idx_cst_claim_id", columnList = "claimId"),
        @Index(name = "idx_cst_occurred_at", columnList = "occurredAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimStatusTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long claimId;

    @Column(nullable = false)
    private String fromStatus;

    @Column(nullable = false)
    private String toStatus;

    @Column(length = 1000)
    private String notes;

    /** The actor who triggered this transition (user email or "SYSTEM"). */
    private String changedBy;

    /** Estimated/actual remaining processing days for customer display. */
    private Integer estimatedDaysRemaining;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime occurredAt;
}
