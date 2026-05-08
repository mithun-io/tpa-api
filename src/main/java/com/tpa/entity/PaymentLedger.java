package com.tpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Immutable payment ledger entry.
 * Every payment state change creates a new ledger row — the ledger is append-only.
 */
@Entity
@Table(name = "payment_ledger", indexes = {
        @Index(name = "idx_pl_claim_id", columnList = "claimId"),
        @Index(name = "idx_pl_payment_id", columnList = "paymentId"),
        @Index(name = "idx_pl_event_type", columnList = "eventType")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long claimId;

    /** FK to payments.id — null for failed/retried entries. */
    private Long paymentId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    @Builder.Default
    private String currency = "INR";

    /**
     * Event type: PAYMENT_CREATED | PAYMENT_VERIFIED | PAYMENT_FAILED |
     *             PAYMENT_REFUNDED | PAYMENT_RECONCILED | INSTANT_PAYOUT
     */
    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String status;

    private String razorpayOrderId;
    private String razorpayPaymentId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Actor who initiated this ledger event. */
    private String initiatedBy;

    /** SHA-256 of this record's payload for tamper detection. */
    @Column(length = 64)
    private String integrityHash;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
