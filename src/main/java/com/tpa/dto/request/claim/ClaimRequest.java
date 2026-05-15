package com.tpa.dto.request.claim;

import com.tpa.enums.PolicyStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClaimRequest {

    @NotBlank(message = "Policy id is required")
    private String policyId;

    @NotBlank(message = "Policy name is required")
    private String policyName;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotNull(message = "Policy number is required")
    private PolicyStatus policyStatus;

    @NotBlank(message = "claim form is present or not present?")
    private Boolean claimFormPresent;

    @NotBlank(message = "Patient name is required on claim form")
    private String claimFormPatientName;

    @NotBlank(message = "Hospital name is required")
    private String claimFormHospitalName;

    @NotNull(message = "Admission date is required")
    private LocalDate claimFormAdmissionDate;

    @NotNull(message = "Discharge date is required")
    private LocalDate claimFormDischargeDate;

    @NotBlank(message = "combined document is present or not present?")
    private Boolean combinedDocumentPresent;

    @NotBlank(message = "Patient name is required on combined document")
    private String combinedDocPatientName;

    @NotBlank(message = "Hospital name is required on combined document")
    private String combinedDocHospitalName;

    @NotNull(message = "Admission date is required")
    private LocalDate combinedDocAdmissionDate;

    @NotNull(message = "Discharge date is required")
    private LocalDate combinedDocDischargeDate;

    @NotNull(message = "Claimed amount is required")
    @Positive(message = "Claimed amount must be positive")
    private Double claimedAmount;

    @NotNull(message = "Total bill amount is required")
    @Positive(message = "Total bill amount must be positive")
    private Double totalBillAmount;

    @NotBlank(message = "Carrier name is required")
    private String carrierName;

    private String claimType;

    private String diagnosis;

    @NotBlank(message = "Bill number is required")
    private String billNumber;

    @NotNull(message = "Bill date is required")
    private LocalDate billDate;
}
