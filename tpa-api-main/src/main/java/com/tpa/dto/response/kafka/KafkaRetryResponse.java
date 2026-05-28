package com.tpa.dto.response.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class KafkaRetryResponse {

    private Boolean success;

    private String eventId;

    private String message;

    private LocalDateTime retriedAt;
}