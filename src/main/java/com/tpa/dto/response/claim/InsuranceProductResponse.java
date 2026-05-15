package com.tpa.dto.response.claim;

import com.tpa.enums.PolicyType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InsuranceProductResponse {

    private Long id;

    private Long carrierId;

    private String carrierName;

    private String productName;

    private String productCode;

    private PolicyType policyType;

    private Double coverageAmount;

    private Double premiumAmount;

    private Integer waitingPeriodDays;

    private Boolean active;
}
