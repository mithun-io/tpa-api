package com.tpa.service;

import com.tpa.dto.request.ai.AiValidationRequest;
import com.tpa.dto.response.analytics.AiAnalysisResponse;
import com.tpa.dto.response.auth.DocumentValidationResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AiClaimAssistantService {

    AiAnalysisResponse analyzeClaim(Long claimId, String prompt);

    String generateClaimSummary(Long claimId);

    AiAnalysisResponse validatePreClaim(AiValidationRequest aiValidationRequest);

    DocumentValidationResponse validateDocument(MultipartFile multipartFile, String documentType);
}
