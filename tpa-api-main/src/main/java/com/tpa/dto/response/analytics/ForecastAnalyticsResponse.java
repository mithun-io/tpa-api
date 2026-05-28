package com.tpa.dto.response.analytics;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastAnalyticsResponse {

    private Double dailyAverageLast30Days;

    private Long forecastNext7Days;

    private Long forecastNext30Days;

    private List<Map<String, Object>> historicalData;

    private String generatedAt;
}
