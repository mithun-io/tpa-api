package com.tpa.dto.response;

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

    private Long claimId;

    private String policyNumber;

    private String status;

    private Double amount;

    private Double totalBillAmount;

    private String claimType;

    private String diagnosis;

    private String hospitalName;

    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private LocalDateTime createdDate;

    private LocalDateTime processedDate;

    private String rejectionReason;

    private String reviewNotes;

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    private PatientInfo patient;

    private FraudInfo fraud;

    private PolicyInfo policy;

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
