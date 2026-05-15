package com.tpa.dto.response.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaTopicResponse {

    private String name;

    private Integer partitions;

    private Boolean dlq;
}