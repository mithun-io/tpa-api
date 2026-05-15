package com.tpa.dto.request.medical;

import lombok.Data;

@Data
public class MedicalValidationRequest {

    private String icdCode;

    private String diagnosis;

    private Double claimedAmount;
}