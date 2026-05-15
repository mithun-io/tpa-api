package com.tpa.dto.response.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaStageBreakdownResponse {

    private Long claimUploaded;

    private Long ocrCompleted;

    private Long aiAnalysisDone;

    private Long ruleEvaluated;

    private Long adminApproved;

    private Long carrierApproved;

    private Long paymentInitiated;

    private Long paymentCompleted;

    private Long rejected;
}