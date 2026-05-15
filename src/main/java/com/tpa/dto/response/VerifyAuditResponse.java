package com.tpa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyAuditResponse {

    private Long claimId;

    private boolean chainIntact;

    private int totalRecords;

    private String verifiedAt;

    private String message;
}