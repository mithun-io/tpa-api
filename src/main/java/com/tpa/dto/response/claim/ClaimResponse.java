package com.tpa.dto.response.claim;

import com.tpa.enums.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {

    private Long id;

    private String userName;

    private String userEmail;

    private String patientName;

    private String hospitalName;

    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private Double totalBillAmount;

    private String policyId;

    private String policyNumber;

    private String carrierName;

    private ClaimStatus claimStatus;

    private String claimType;

    private String diagnosis;

    private Double amount;

    private String rejectionReason;

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    private String reviewNotes;

    private LocalDateTime createdDate;

    private LocalDateTime processedDate;

    private Double riskScore;

    private String riskFlags;

    private Integer healthScore;

    private String riskLevel;

}
