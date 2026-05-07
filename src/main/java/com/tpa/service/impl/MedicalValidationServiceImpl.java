package com.tpa.service.impl;

import com.tpa.service.MedicalValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class MedicalValidationServiceImpl implements MedicalValidationService {

    // Mock ICD-10 Dictionary for demo purposes
    private static final Map<String, String> ICD_DICTIONARY = Map.of(
            "I10", "Essential (primary) hypertension",
            "E11.9", "Type 2 diabetes mellitus without complications",
            "J45.909", "Unspecified asthma, uncomplicated",
            "M54.5", "Low back pain",
            "Z00.00", "Encounter for general adult medical examination without abnormal findings",
            "C34.90", "Malignant neoplasm of unspecified part of unspecified bronchus or lung",
            "I21.9", "Acute myocardial infarction, unspecified"
    );

    private static final Set<String> HIGH_RISK_CODES = Set.of(
            "C34.90", "I21.9", "A41.9", "G00.9" // Cancer, Heart Attack, Sepsis, Meningitis
    );

    @Override
    public List<String> validateIcdCode(String icdCode, String diagnosis) {
        List<String> issues = new ArrayList<>();
        
        if (icdCode == null || icdCode.isBlank()) {
            issues.add("Medical code (ICD-10) is missing from the discharge summary");
            return issues;
        }

        String standardizedCode = icdCode.toUpperCase().trim();
        String description = ICD_DICTIONARY.get(standardizedCode);

        if (description == null) {
            log.warn("Unknown ICD-10 code detected: {}", icdCode);
            // We don't necessarily flag as error if not in our mock dictionary, 
            // but in a real system, we'd validate against a full database.
        } else {
            if (diagnosis != null && !diagnosis.isBlank()) {
                // Basic keyword matching for consistency check
                String firstWord = description.split(" ")[0].toLowerCase();
                if (!diagnosis.toLowerCase().contains(firstWord)) {
                    issues.add(String.format("Potential mismatch: ICD-10 code %s (%s) does not seem to align with stated diagnosis '%s'", 
                        standardizedCode, description, diagnosis));
                }
            }
        }

        return issues;
    }

    @Override
    public boolean isHighRiskDiagnosis(String icdCode) {
        if (icdCode == null) return false;
        return HIGH_RISK_CODES.contains(icdCode.toUpperCase().trim());
    }
}
