package com.tpa.dto.response.claim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkClaimProcessResponse {

    private int totalProcessed;

    private int success;

    private int failed;
}