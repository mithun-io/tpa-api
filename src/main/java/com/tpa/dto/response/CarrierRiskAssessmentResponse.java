package com.tpa.dto.response;

import com.tpa.enums.AiRecommendation;
import com.tpa.enums.AiRiskStatus;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarrierRiskAssessmentResponse {

    private Double aiRiskScore;

    private AiRiskStatus aiRiskStatus;

    private AiRecommendation aiRecommendation;

    private String aiReasoning;

    private LocalDateTime analyzedAt;
}
