package com.tpa.dto.response.analytics;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudTrendResponse {

    private Map<String, Long> riskDistribution;

    private Double fraudRate;

    private Map<String, Long> topRiskHospitals;

    private Double averageFraudScore;

    private String generatedAt;
}
