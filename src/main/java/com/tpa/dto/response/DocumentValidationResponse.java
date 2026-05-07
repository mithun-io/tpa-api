package com.tpa.dto.response;

import com.tpa.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentValidationResponse {

    private DocumentStatus status;

    private List<String> issues;

    private Integer confidenceScore;

    private String icdCode;
}
