package com.tpa.dto.response.kafka;

import com.tpa.entity.EventAuditLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class KafkaDlqResponse {

    private List<EventAuditLog> dlqMessages;

    private Integer totalDlqMessages;

    private Integer page;

    private Integer size;

    private LocalDateTime retrievedAt;
}