package com.tpa.dto.response.medical;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MedicalValidationResponse {

    private String icdCode;

    private String diagnosis;

    private List<String> validationIssues;

    private List<String> upcodingWarnings;

    private boolean highRisk;

    private int medicalRiskScore;

    private String overallStatus;

    private LocalDateTime validatedAt;
}