package com.tpa.dto.response.claim;

import com.tpa.enums.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDecisionResponse {

    private ClaimStatus claimStatus;

    @Builder.Default
    private List<String> reasons = new ArrayList<>();
}