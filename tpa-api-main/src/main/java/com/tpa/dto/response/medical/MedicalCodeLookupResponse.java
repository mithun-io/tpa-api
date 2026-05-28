package com.tpa.dto.response.medical;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MedicalCodeLookupResponse {

    private String code;

    private boolean found;

    private String description;

    private boolean highRisk;

    private int medicalRiskScore;

    private LocalDateTime retrievedAt;
}