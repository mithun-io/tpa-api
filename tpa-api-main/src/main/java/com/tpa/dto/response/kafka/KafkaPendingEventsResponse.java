package com.tpa.dto.response.kafka;

import com.tpa.entity.EventAuditLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class KafkaPendingEventsResponse {

    private List<EventAuditLog> pendingEvents;

    private Integer count;

    private LocalDateTime retrievedAt;
}