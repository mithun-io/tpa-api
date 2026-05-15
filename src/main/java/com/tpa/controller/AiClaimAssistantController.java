package com.tpa.controller;

import com.tpa.dto.response.ApiResponse;
import com.tpa.dto.response.DocumentValidationResponse;
import com.tpa.service.AiClaimAssistantService;
import com.tpa.dto.request.AiValidationRequest;
import com.tpa.dto.response.AiAnalysisResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiClaimAssistantController {

    private final AiClaimAssistantService aiClaimAssistantService;

    @PostMapping("/analyze/{claimId}")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'FMG_EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<AiAnalysisResponse>> analyzeClaim(@PathVariable Long claimId, @RequestBody(required = false) Map<String, String> request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim analysis completed successfully", aiClaimAssistantService.analyzeClaim(claimId, request != null ? request.get("prompt") : null), 200));
    }

    @PostMapping("/claims/{id}/generate-summary")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'CARRIER_USER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateClaimSummary(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim summary generated successfully", Map.of("summary", aiClaimAssistantService.generateClaimSummary(id)), 200));
    }

    @PostMapping("/validate-claim")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AiAnalysisResponse>> validateClaim(@Valid @RequestBody AiValidationRequest aiValidationRequest) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim validation completed successfully", aiClaimAssistantService.validatePreClaim(aiValidationRequest), 200));
    }

    @PostMapping(value = "/validate-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DocumentValidationResponse>> validateDocument(@RequestParam("file") MultipartFile multipartFile, @RequestParam("documentType") String documentType) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Document validation completed successfully", aiClaimAssistantService.validateDocument(multipartFile, documentType), 200));
    }
}
