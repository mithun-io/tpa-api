package com.tpa.dto.response.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class KafkaPipelineHealthResponse {

    private Long totalEventsProcessed;

    private Long pendingEvents;

    private Long totalEvents;

    private Double successRate;

    private Integer dlqMessageCount;

    private String kafkaStatus;

    private KafkaStageBreakdownResponse stageBreakdown;

    private LocalDateTime retrievedAt;
}