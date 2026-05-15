package com.tpa.dto.response.analytics;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpa.enums.Verdict;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResponse {

    private Verdict verdict;

    private double confidence;

    private double riskScore;

    private ValidationChecks validationChecks;

    private FinancialSummary financialSummary;

    private List<String> flags;

    private String recommendation;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm a")
    private LocalDateTime generatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationChecks {

        private boolean policyActive;

        private boolean documentsComplete;

        private boolean withinLimit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialSummary {

        private BigDecimal claimedAmount;

        private BigDecimal eligibleAmount;
    }
}
