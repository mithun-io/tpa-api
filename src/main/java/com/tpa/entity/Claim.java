package com.tpa.entity;

import com.tpa.enums.ClaimStatus;
import com.tpa.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String policyNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime processedDate;

    @Column(length = 1000)
    private String rejectionReason;

    private String patientName;

    private String hospitalName;

    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private Double totalBillAmount;

    private String policyId;

    private String carrierName;

    private String policyName;

    private String claimType;

    private String diagnosis;

    private String billNumber;

    private String icdCode;

    private LocalDate billDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    private String reviewedBy;

    private String assignedTo;

    private LocalDateTime reviewedAt;

    @Column(length = 1000)
    private String reviewNotes;

    private Double riskScore;

    @Column(length = 1000)
    private String riskFlags;
    
    private Integer healthScore;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
    
    @Column(length = 2000)
    private String aiSummary;

    // ── Multi-Tenant ──────────────────────────────────────────────────────────
    @Column
    private String tenantId;

    // ── SLA & Escalation ─────────────────────────────────────────────────────
    private LocalDateTime slaDeadline;

    @Column(nullable = false)
    @Builder.Default
    private Boolean escalated = false;

    private LocalDateTime escalatedAt;

    private String escalationReason;

    // ── Fraud Decision ────────────────────────────────────────────────────────
    private Double fraudScore;

    @Column(length = 500)
    private String fraudFlags;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (tenantId == null) tenantId = "default";
        // Default SLA: 48 hours from creation
        if (slaDeadline == null) slaDeadline = createdDate.plusHours(48);
    }
}
