package com.tpa.dto.request.ai;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiValidationRequest {

    @NotEmpty(message = "Patient Name is required")
    private String patientName;

    @NotEmpty(message = "Hospital Name is required")
    private String hospitalName;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotNull(message = "Claimed amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotEmpty(message = "Diagnosis details is required")
    private String diagnosis;

    @NotBlank(message = "Admission date is required")
    private String admissionDate;

    @NotBlank(message = "Discharge date is required")
    private String dischargeDate;
}
