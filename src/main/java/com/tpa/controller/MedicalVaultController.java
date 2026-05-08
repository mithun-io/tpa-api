package com.tpa.controller;

import com.tpa.service.impl.MedicalValidationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Medical Vault Controller — Admin management of ICD-10 codes.
 * Provides:
 * - Browse/search ICD-10 dictionary
 * - Validate a single code
 * - Upcoding risk analysis for a given code + amount
 * - High-risk diagnosis registry management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/medical")
@RequiredArgsConstructor
public class MedicalVaultController {

    private final MedicalValidationServiceImpl medicalValidationService;

    // ── ICD-10 Code Lookup ────────────────────────────────────────────────────

    @GetMapping("/codes/lookup")
    public ResponseEntity<Map<String, Object>> lookupCode(@RequestParam String code) {
        String description = medicalValidationService.getIcdDescription(code);
        boolean isHighRisk = medicalValidationService.isHighRiskDiagnosis(code);
        int riskScore = medicalValidationService.calculateMedicalRiskScore(code);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", code.toUpperCase().trim());
        result.put("found", description != null);
        result.put("description", description != null ? description : "Unknown ICD-10 code");
        result.put("highRisk", isHighRisk);
        result.put("medicalRiskScore", riskScore);
        result.put("retrievedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    // ── Validate ICD Code + Diagnosis consistency ─────────────────────────────

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCodeAndDiagnosis(
            @RequestBody Map<String, String> request) {

        String icdCode = request.get("icdCode");
        String diagnosis = request.get("diagnosis");
        Double claimedAmount = request.containsKey("claimedAmount")
                ? Double.parseDouble(request.get("claimedAmount")) : null;

        List<String> validationIssues = medicalValidationService.validateIcdCode(icdCode, diagnosis);
        List<String> upcodingWarnings = medicalValidationService.detectUpcoding(icdCode, claimedAmount);
        boolean isHighRisk = medicalValidationService.isHighRiskDiagnosis(icdCode);
        int riskScore = medicalValidationService.calculateMedicalRiskScore(icdCode);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("icdCode", icdCode);
        result.put("diagnosis", diagnosis);
        result.put("validationIssues", validationIssues);
        result.put("upcodingWarnings", upcodingWarnings);
        result.put("isHighRisk", isHighRisk);
        result.put("medicalRiskScore", riskScore);
        result.put("overallStatus", validationIssues.isEmpty() && upcodingWarnings.isEmpty() ? "CLEAN" : "FLAGGED");
        result.put("validatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    // ── Batch validate ICD codes ──────────────────────────────────────────────

    @PostMapping("/validate/batch")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'CARRIER_USER')")
    public ResponseEntity<Map<String, Object>> batchValidate(
            @RequestBody List<Map<String, String>> items) {

        List<Map<String, Object>> results = new ArrayList<>();
        int flaggedCount = 0;

        for (Map<String, String> item : items) {
            String code = item.get("icdCode");
            String diagnosis = item.get("diagnosis");

            List<String> issues = medicalValidationService.validateIcdCode(code, diagnosis);
            boolean highRisk = medicalValidationService.isHighRiskDiagnosis(code);

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("icdCode", code);
            entry.put("diagnosis", diagnosis);
            entry.put("issues", issues);
            entry.put("highRisk", highRisk);
            entry.put("status", issues.isEmpty() ? "OK" : "FLAGGED");
            results.add(entry);

            if (!issues.isEmpty() || highRisk) flaggedCount++;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalValidated", items.size());
        response.put("flaggedCount", flaggedCount);
        response.put("cleanCount", items.size() - flaggedCount);
        response.put("results", results);
        response.put("validatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // ── Upcoding Risk Analyzer ────────────────────────────────────────────────

    @GetMapping("/upcoding/risk")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'CARRIER_USER')")
    public ResponseEntity<Map<String, Object>> analyzeUpcodingRisk(
            @RequestParam String icdCode,
            @RequestParam(required = false) Double claimedAmount) {

        List<String> warnings = medicalValidationService.detectUpcoding(icdCode, claimedAmount);
        String description = medicalValidationService.getIcdDescription(icdCode);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("icdCode", icdCode);
        result.put("icdDescription", description);
        result.put("claimedAmount", claimedAmount);
        result.put("upcodingRisk", !warnings.isEmpty());
        result.put("warnings", warnings);
        result.put("riskLevel", warnings.isEmpty() ? "LOW" : "HIGH");
        result.put("analyzedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    // ── High-Risk Diagnosis Registry ──────────────────────────────────────────

    @GetMapping("/high-risk/codes")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'CARRIER_USER')")
    public ResponseEntity<Map<String, Object>> getHighRiskCodes() {
        // Return a curated list of high-risk ICD codes with descriptions
        List<Map<String, String>> codes = List.of(
                Map.of("code", "C34.90", "description", "Malignant neoplasm of bronchus/lung", "category", "Oncology"),
                Map.of("code", "C50.911", "description", "Malignant neoplasm of right female breast", "category", "Oncology"),
                Map.of("code", "C18.9", "description", "Malignant neoplasm of colon", "category", "Oncology"),
                Map.of("code", "C61", "description", "Malignant neoplasm of prostate", "category", "Oncology"),
                Map.of("code", "C80.1", "description", "Malignant neoplasm, unspecified", "category", "Oncology"),
                Map.of("code", "I21.9", "description", "Acute myocardial infarction", "category", "Cardiac"),
                Map.of("code", "I50.9", "description", "Heart failure, unspecified", "category", "Cardiac"),
                Map.of("code", "I63.9", "description", "Cerebral infarction", "category", "Neurological"),
                Map.of("code", "A41.9", "description", "Sepsis, unspecified", "category", "Infectious"),
                Map.of("code", "G00.9", "description", "Bacterial meningitis, unspecified", "category", "Neurological"),
                Map.of("code", "J96.01", "description", "Acute respiratory failure with hypoxia", "category", "Respiratory"),
                Map.of("code", "Z51.11", "description", "Encounter for antineoplastic chemotherapy", "category", "Oncology")
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("highRiskCodes", codes);
        result.put("totalCodes", codes.size());
        result.put("retrievedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }
}
