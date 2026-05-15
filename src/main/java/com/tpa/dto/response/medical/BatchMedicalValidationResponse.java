package com.tpa.dto.response.medical;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BatchMedicalValidationResponse {

    private int totalValidated;

    private int flaggedCount;

    private int cleanCount;

    private List<MedicalValidationResponse> results;

    private LocalDateTime validatedAt;
}