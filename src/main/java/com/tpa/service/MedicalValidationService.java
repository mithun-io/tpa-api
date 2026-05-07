package com.tpa.service;

import com.tpa.entity.Claim;
import java.util.List;

public interface MedicalValidationService {
    List<String> validateIcdCode(String icdCode, String diagnosis);
    boolean isHighRiskDiagnosis(String icdCode);
}
