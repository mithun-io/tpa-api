package com.tpa.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDataRequest {
    private Boolean claimFormPresent;
    private Boolean combinedDocumentPresent;
    
    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    private String policyStatus;

    @NotBlank(message = "Patient name is required on claim form")
    private String claimFormPatientName;

    private String combinedDocPatientName;

    @NotBlank(message = "Hospital name is required")
    private String claimFormHospitalName;

    private String combinedDocHospitalName;

    @NotNull(message = "Admission date is required")
    private LocalDate claimFormAdmissionDate;

    private LocalDate combinedDocAdmissionDate;
    
    private LocalDate claimFormDischargeDate;

    private LocalDate combinedDocDischargeDate;

    @NotNull(message = "Claimed amount is required")
    @Positive(message = "Claimed amount must be positive")
    private Double claimedAmount;

    @NotNull(message = "Total bill amount is required")
    @Positive(message = "Total bill amount must be positive")
    private Double totalBillAmount;
    
    private Boolean isDuplicate;

    private String policyId;

    @NotBlank(message = "Carrier name is required")
    private String carrierName;

    private String policyName;

    private String claimType;

    private String diagnosis;

    @NotBlank(message = "Bill number is required")
    private String billNumber;

    @NotNull(message = "Bill date is required")
    private LocalDate billDate;
}
