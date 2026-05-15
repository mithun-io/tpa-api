package com.tpa.dto.response;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalAnalyticsResponse {

    private Map<String, Long> topHospitalsByVolume;

    private Map<String, Double> topHospitalsByAmount;

    private int totalUniqueHospitals;

    private String generatedAt;
}
