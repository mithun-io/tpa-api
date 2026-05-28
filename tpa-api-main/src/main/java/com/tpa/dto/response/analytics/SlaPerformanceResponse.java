package com.tpa.dto.response.analytics;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlaPerformanceResponse {

    private long totalClaims;

    private long withinSla;

    private long slaBreached;

    private long escalated;

    private Double slaComplianceRate;

    private Double avgProcessingHours;

    private String generatedAt;
}