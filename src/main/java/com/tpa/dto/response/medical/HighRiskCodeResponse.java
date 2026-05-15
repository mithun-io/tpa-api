package com.tpa.dto.response.medical;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HighRiskCodeResponse {

    private int totalCodes;

    private List<HighRiskCode> codes;

    private LocalDateTime retrievedAt;

    @Data
    @Builder
    public static class HighRiskCode {

        private String code;

        private String description;
    }
}