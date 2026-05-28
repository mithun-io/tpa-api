package com.tpa.service.impl;

import com.tpa.dto.request.medical.MedicalValidationRequest;
import com.tpa.dto.response.medical.*;
import com.tpa.service.MedicalValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class MedicalValidationServiceImpl implements MedicalValidationService {

    private static final Map<String, String> ICD_DICTIONARY = new HashMap<>() {{
        put("I10", "Essential (primary) hypertension");
        put("I21.9", "Acute myocardial infarction");
        put("E11.9", "Type 2 diabetes mellitus");
        put("J18.9", "Pneumonia");
        put("C34.90", "Malignant neoplasm of lung");
        put("A41.9", "Sepsis");
        put("J96.01", "Acute respiratory failure");
        put("G00.9", "Bacterial meningitis");
        put("Z51.11", "Chemotherapy encounter");
    }};

    private static final Set<String> HIGH_RISK_CODES = Set.of(
            "C34.90",
            "A41.9",
            "J96.01",
            "G00.9",
            "I21.9",
            "Z51.11"
    );

    private static final Map<String, List<String>> UPCODING_SUSPECTS = Map.of(
            "Z00.00", List.of("I21.9", "C34.90"),
            "J06.9", List.of("J18.9", "J96.01")
    );

    private int calculateMedicalRiskScore(String icdCode) {
        if (icdCode == null || icdCode.isBlank()) {
            return 50;
        }

        String normalizedCode = icdCode.toUpperCase().trim();

        if (HIGH_RISK_CODES.contains(normalizedCode)) {
            return 80;
        }

        if (ICD_DICTIONARY.containsKey(normalizedCode)) {
            return 20;
        }

        return 60;
    }

    private List<String> detectUpcoding(String icdCode, Double claimedAmount) {
        List<String> warnings = new ArrayList<>();

        if (icdCode == null) {
            return warnings;
        }

        String normalizedCode = icdCode.toUpperCase().trim();

        for (Map.Entry<String, List<String>> entry : UPCODING_SUSPECTS.entrySet()) {
            String cheapCode = entry.getKey();

            if (normalizedCode.equals(cheapCode) && claimedAmount != null && claimedAmount > 10000) {
                warnings.add("Potential upcoding detected for high-value claim");
            }
        }
        return warnings;
    }

    private String getIcdDescription(String icdCode) {
        if (icdCode == null) {
            return null;
        }
        return ICD_DICTIONARY.get(icdCode.toUpperCase().trim());
    }

    @Override
    public List<String> validateIcdCode(String icdCode, String diagnosis) {
        List<String> issues = new ArrayList<>();

        if (icdCode == null || icdCode.isBlank()) {
            issues.add("ICD code is missing");

            return issues;
        }

        String normalizedCode = icdCode.toUpperCase().trim();

        String description = ICD_DICTIONARY.get(normalizedCode);

        if (description == null) {
            issues.add("Unknown ICD-10 code");
            return issues;
        }

        if (diagnosis != null && !diagnosis.isBlank()) {

            boolean matches = Arrays.stream(description.split(" "))
                    .filter(word -> word.length() > 4)
                    .anyMatch(word -> diagnosis.toLowerCase().contains(word.toLowerCase()));

            if (!matches) {
                issues.add("Diagnosis does not align with ICD description");
            }
        }

        return issues;
    }

    @Override
    public boolean isHighRiskDiagnosis(String icdCode) {
        if (icdCode == null) {
            return false;
        }

        return HIGH_RISK_CODES.contains(icdCode.toUpperCase().trim());
    }

    @Override
    public MedicalCodeLookupResponse lookupCode(String code) {
        String normalizedCode = code.toUpperCase().trim();
        String description = ICD_DICTIONARY.get(normalizedCode);

        boolean found = description != null;

        return MedicalCodeLookupResponse.builder()
                .code(normalizedCode)
                .found(found)
                .description(found ? description : "Unknown ICD-10 code")
                .highRisk(isHighRiskDiagnosis(normalizedCode))
                .medicalRiskScore(calculateMedicalRiskScore(normalizedCode))
                .retrievedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public MedicalValidationResponse validateMedicalData(MedicalValidationRequest medicalValidationRequest) {

        List<String> validationIssues = validateIcdCode(medicalValidationRequest.getIcdCode(), medicalValidationRequest.getDiagnosis());

        List<String> upcodingWarnings = detectUpcoding(medicalValidationRequest.getIcdCode(), medicalValidationRequest.getClaimedAmount());

        boolean highRisk = isHighRiskDiagnosis(medicalValidationRequest.getIcdCode());

        int riskScore = calculateMedicalRiskScore(medicalValidationRequest.getIcdCode());

        return MedicalValidationResponse.builder()
                .icdCode(medicalValidationRequest.getIcdCode())
                .diagnosis(medicalValidationRequest.getDiagnosis())
                .validationIssues(validationIssues)
                .upcodingWarnings(upcodingWarnings)
                .highRisk(highRisk)
                .medicalRiskScore(riskScore)
                .overallStatus(validationIssues.isEmpty() && upcodingWarnings.isEmpty() ? "CLEAN" : "FLAGGED")
                .validatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public BatchMedicalValidationResponse batchValidate(List<MedicalValidationRequest> medicalValidationRequests) {
        List<MedicalValidationResponse> medicalValidationResponses = new ArrayList<>();

        int flaggedCount = 0;

        for (MedicalValidationRequest medicalValidationRequest : medicalValidationRequests) {
            MedicalValidationResponse medicalValidationResponse = validateMedicalData(medicalValidationRequest);

            medicalValidationResponses.add(medicalValidationResponse);

            if (!medicalValidationResponse.getValidationIssues().isEmpty() || medicalValidationResponse.isHighRisk()) {
                flaggedCount++;
            }
        }

        return BatchMedicalValidationResponse.builder()
                .totalValidated(medicalValidationRequests.size())
                .flaggedCount(flaggedCount)
                .cleanCount(medicalValidationRequests.size() - flaggedCount)
                .results(medicalValidationResponses)
                .validatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public UpcodingRiskResponse analyzeUpcodingRisk(String icdCode, Double claimedAmount) {
        List<String> warnings = detectUpcoding(icdCode, claimedAmount);

        return UpcodingRiskResponse.builder()
                .icdCode(icdCode)
                .icdDescription(getIcdDescription(icdCode))
                .claimedAmount(claimedAmount)
                .upcodingRisk(!warnings.isEmpty())
                .warnings(warnings)
                .riskLevel(warnings.isEmpty() ? "LOW" : "HIGH")
                .analyzedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public HighRiskCodeResponse getHighRiskCodes() {

        List<HighRiskCodeResponse.HighRiskCode> highRiskCodes = HIGH_RISK_CODES.stream()
                        .map(code -> HighRiskCodeResponse.HighRiskCode.builder().code(code).description(ICD_DICTIONARY.get(code)).build())
                        .toList();

        return HighRiskCodeResponse.builder()
                .totalCodes(highRiskCodes.size())
                .codes(highRiskCodes)
                .retrievedAt(LocalDateTime.now())
                .build();
    }
}