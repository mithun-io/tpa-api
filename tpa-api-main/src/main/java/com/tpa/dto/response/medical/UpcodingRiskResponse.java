package com.tpa.dto.response.medical;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UpcodingRiskResponse {

    private String icdCode;

    private String icdDescription;

    private Double claimedAmount;

    private boolean upcodingRisk;

    private List<String> warnings;

    private String riskLevel;

    private LocalDateTime analyzedAt;
}