package com.tpa.kafka.event;

import com.tpa.enums.ClaimStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Full lifecycle event DTO for the Kafka claim pipeline.
 * Each stage emits this event with its specific stage set.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLifecycleEvent {

    public enum Stage {
        CLAIM_UPLOADED,
        OCR_COMPLETED,
        AI_ANALYSIS_DONE,
        RULE_EVALUATED,
        ADMIN_APPROVED,
        CARRIER_APPROVED,
        PAYMENT_INITIATED,
        PAYMENT_COMPLETED,
        REJECTED
    }

    /** Unique idempotency key — prevents duplicate processing. */
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    private Long claimId;
    private String policyNumber;
    private String customerEmail;
    private Stage stage;
    private ClaimStatus claimStatus;
    private String message;
    private String metadata;

    /** Number of times this event has been retried. */
    @Builder.Default
    private int retryCount = 0;

    @Builder.Default
    private LocalDateTime eventTime = LocalDateTime.now();
}
