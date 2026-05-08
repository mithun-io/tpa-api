package com.tpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rule_configs", indexes = {
        @Index(name = "idx_rule_key", columnList = "ruleKey"),
        @Index(name = "idx_rule_active", columnList = "active"),
        @Index(name = "idx_rule_priority", columnList = "priority")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ruleKey;

    /**
     * Legacy simple value (e.g., "50000") — used by non-Groovy rules.
     */
    @Column(nullable = false)
    private String ruleValue;

    private String description;

    /**
     * Groovy script body — receives binding variables:
     *   ClaimDataRequest claim, ClaimDecisionResponse decision
     * The script should set decision.status and append to decision.reasons.
     */
    @Column(columnDefinition = "TEXT")
    private String groovyScript;

    /**
     * Rule type: SIMPLE | GROOVY
     */
    @Column(nullable = false)
    @Builder.Default
    private String ruleType = "SIMPLE";

    /**
     * Lower number = higher priority. Rules are evaluated in ascending order.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 100;

    /**
     * Version counter — incremented on each update.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer version = 1;

    /**
     * Whether this rule is active. Inactive rules are skipped during evaluation.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Simulation mode — rule is evaluated but does NOT affect claim status.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean simulationMode = false;

    /**
     * Category: ELIGIBILITY | AMOUNT | FRAUD | MEDICAL | SLA | DEFAULT
     */
    @Column
    @Builder.Default
    private String category = "DEFAULT";

    @Column
    private String lastUpdatedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
