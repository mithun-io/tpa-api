package com.tpa.dto.response.claim;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimDocumentResponse {

    private Long id;

    private String fileName;

    private String fileType;

    private String documentType;

    private String validationStatus;

    private String validationIssues;

    private Double confidenceScore;

    private String fileUrl;
}