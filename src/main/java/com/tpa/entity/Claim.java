package com.tpa.entity;

import com.tpa.enums.ClaimStatus;
import com.tpa.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    private String patientName;

    private String hospitalName;

    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private Double totalBillAmount;

    private String policyId;

    @Column(nullable = false)
    private String policyNumber;

    private String policyName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus claimStatus;

    private String carrierName;

    private String claimType;

    private String diagnosis;

    private String billNumber;

    private String icdCode;

    private Double amount;

    private LocalDate billDate;

    private String reviewedBy;

    private String assignedTo;

    private LocalDateTime reviewedAt;

    @Column(length = 1000)
    private String reviewNotes;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime processedDate;

    @Column(length = 1000)
    private String rejectionReason;

    private Double riskScore;

    @Column(length = 1000)
    private String riskFlags;
    
    private Double healthScore;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
    

    // Tenant
    @Column
    private String tenantId;

    // SLA Tracker
    private LocalDateTime slaDeadline;

    @Column(nullable = false)
    @Builder.Default
    private Boolean escalated = false;

    private LocalDateTime escalatedAt;

    private String escalationReason;

    // Fraud Decision
    private Double fraudScore;

    @Column(length = 500)
    private String fraudFlags;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (tenantId == null) tenantId = "default";
        if (createdDate == null) createdDate = LocalDateTime.now();
        // Default SLA: 48 hours from creation
        if (slaDeadline == null) slaDeadline = createdDate.plusHours(48);
    }
}
