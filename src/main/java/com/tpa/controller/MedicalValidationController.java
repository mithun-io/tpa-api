package com.tpa.controller;

import com.tpa.dto.request.medical.MedicalValidationRequest;
import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.medical.BatchMedicalValidationResponse;
import com.tpa.dto.response.medical.HighRiskCodeResponse;
import com.tpa.dto.response.medical.MedicalCodeLookupResponse;
import com.tpa.dto.response.medical.MedicalValidationResponse;
import com.tpa.dto.response.medical.UpcodingRiskResponse;
import com.tpa.service.MedicalValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medical")
@RequiredArgsConstructor
public class MedicalValidationController {

    private final MedicalValidationService medicalValidationService;

    @GetMapping("/codes/lookup")
    public ResponseEntity<ApiResponse<MedicalCodeLookupResponse>> lookupCode(@RequestParam String code) {
        return ResponseEntity.ok(new ApiResponse<>(true, "ICD code retrieved successfully", medicalValidationService.lookupCode(code), 200));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<MedicalValidationResponse>> validateMedicalData(@RequestBody MedicalValidationRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Medical validation completed", medicalValidationService.validateMedicalData(request), 200));
    }

    @PostMapping("/validate/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'CARRIER')")
    public ResponseEntity<ApiResponse<BatchMedicalValidationResponse>> batchValidate(@RequestBody List<MedicalValidationRequest> requests) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Batch validation completed", medicalValidationService.batchValidate(requests), 200));
    }

    @GetMapping("/upcoding/risk")
    @PreAuthorize("hasAnyRole('ADMIN', 'CARRIER')")
    public ResponseEntity<ApiResponse<UpcodingRiskResponse>> analyzeUpcodingRisk(@RequestParam String icdCode, @RequestParam(required = false) Double claimedAmount) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Upcoding risk analyzed", medicalValidationService.analyzeUpcodingRisk(icdCode, claimedAmount), 200));
    }

    @GetMapping("/high-risk/codes")
    @PreAuthorize("hasAnyRole('ADMIN', 'CARRIER')")
    public ResponseEntity<ApiResponse<HighRiskCodeResponse>> getHighRiskCodes() {
        return ResponseEntity.ok(new ApiResponse<>(true, "High-risk ICD codes retrieved successfully", medicalValidationService.getHighRiskCodes(), 200));
    }
}