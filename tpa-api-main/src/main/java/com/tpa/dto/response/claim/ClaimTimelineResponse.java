package com.tpa.dto.response.claim;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClaimTimelineResponse {

    private Long id;

    private Long claimId;

    private String fromStatus;

    private String toStatus;

    private String notes;

    private String changedBy;

    private LocalDateTime occurredAt;
}