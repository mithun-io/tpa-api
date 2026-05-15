package com.tpa.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    private Long totalClaims;

    private Double totalApprovedPayout;

    private Double totalClaimAmount;

    private Map<String, Long> statusDistribution;

    private List<ClaimsPerDayResponse> claimsPerDay;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimsPerDayResponse {

        private String date;

        private Long count;
    }
}