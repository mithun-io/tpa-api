package com.tpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rule_execution_audits", indexes = {
        @Index(name = "idx_rea_claim_id", columnList = "claimId"),
        @Index(name = "idx_rea_rule_key", columnList = "ruleKey"),
        @Index(name = "idx_rea_executed_at", columnList = "executedAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleExecutionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long claimId;

    @Column(nullable = false)
    private String ruleKey;

    private String ruleType;

    private Integer ruleVersion;

    /**
     * Status before this rule ran.
     */
    private String inputStatus;

    /**
     * Status set by this rule (null if rule fired but didn't change status, or was simulation).
     */
    private String outputStatus;

    @Column(columnDefinition = "TEXT")
    private String reasons;

    /**
     * Whether this rule run was a simulation (did not persist changes).
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean simulation = false;

    /**
     * Whether the rule matched/fired.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean fired = false;

    /**
     * Execution time in milliseconds.
     */
    private Long executionTimeMs;

    @Column
    private String executedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime executedAt;
}
