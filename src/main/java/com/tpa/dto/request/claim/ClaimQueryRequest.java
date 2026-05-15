package com.tpa.dto.request.claim;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClaimQueryRequest {

    @NotBlank(message = "message is required")
    private String message;
}
