package com.tpa.dto.request;

import com.tpa.enums.PolicyType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InsuranceProductRequest {

    @NotNull(message = "Carrier id is required")
    private Long carrierId;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    private String productName;

    @NotBlank(message = "Product code is required")
    @Size(max = 50, message = "Product code cannot exceed 50 characters")
    private String productCode;

    @NotNull(message = "Policy type is required")
    private PolicyType policyType;

    @NotNull(message = "Coverage amount is required")
    @Positive(message = "Coverage amount must be positive")
    private Double coverageAmount;

    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium amount must be positive")
    private Double premiumAmount;

    @NotNull(message = "Waiting period is required")
    @Min(value = 0, message = "Waiting period cannot be negative")
    private Integer waitingPeriodDays;

    @NotNull(message = "Active status is required")
    private Boolean active;
}