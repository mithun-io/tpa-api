package com.tpa.dto.request;

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
    
    private String policyNumber;

    private String policyStatus;

    private String claimFormPatientName;

    private String combinedDocPatientName;

    private String claimFormHospitalName;

    private String combinedDocHospitalName;

    private LocalDate claimFormAdmissionDate;

    private LocalDate combinedDocAdmissionDate;
    
    private LocalDate claimFormDischargeDate;

    private LocalDate combinedDocDischargeDate;

    private Double claimedAmount;

    private Double totalBillAmount;
    
    private Boolean isDuplicate;

    private String policyId;

    private String carrierName;

    private String policyName;

    private String claimType;

    private String diagnosis;

    private String billNumber;

    private LocalDate billDate;
}
