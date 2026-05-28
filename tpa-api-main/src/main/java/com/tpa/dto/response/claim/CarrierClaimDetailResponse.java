package com.tpa.dto.response.claim;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PolicyStatus;
import com.tpa.enums.RiskLevel;
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
public class CarrierClaimDetailResponse {

    private String policyNumber;

    private Long claimId;
    private String claimType;
    private ClaimStatus claimStatus;

    private Double amount;
    private Double totalBillAmount;

    private String diagnosis;

    private String hospitalName;

    private LocalDate admissionDate;
    private LocalDate dischargeDate;

    private LocalDateTime createdDate;
    private LocalDateTime processedDate;

    private String rejectionReason;

    private String reviewNotes;
    private String reviewedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm a")
    private LocalDateTime reviewedAt;

    private PatientInfo patientInfo;
    private FraudInfo fraudInfo;
    private PolicyInfo policyInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {

        private String name;

        private String email;

        private String mobile;

        private LocalDate dateOfBirth;

        private String gender;

        private String address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FraudInfo {

        private Double riskScore;

        private RiskLevel riskLevel;

        private Integer healthScore;

        private String riskFlags;

        private String aiSummary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PolicyInfo {

        private String policyNumber;

        private PolicyStatus policyStatus;

        private String reason;
    }
}