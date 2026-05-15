package com.tpa.dto.response.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class KafkaTopicsResponse {

    private List<KafkaTopicResponse> topics;

    private Integer totalTopics;

    private Long dlqTopics;

    private LocalDateTime retrievedAt;
}