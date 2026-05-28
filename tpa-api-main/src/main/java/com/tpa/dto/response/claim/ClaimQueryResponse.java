package com.tpa.dto.response.claim;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClaimQueryResponse {

    private Long id;

    private Long claimId;

    private String username;

    private String message;

    private Boolean carrier;

    private LocalDateTime timestamp;
}