package com.tpa.service;

import com.tpa.dto.request.medical.MedicalValidationRequest;
import com.tpa.dto.response.medical.*;

import java.util.List;

public interface MedicalValidationService {

    List<String> validateIcdCode(String icdCode, String diagnosis);

    boolean isHighRiskDiagnosis(String icdCode);

    MedicalCodeLookupResponse lookupCode(String code);

    MedicalValidationResponse validateMedicalData(MedicalValidationRequest request);

    BatchMedicalValidationResponse batchValidate(List<MedicalValidationRequest> requests);

    UpcodingRiskResponse analyzeUpcodingRisk(String icdCode, Double claimedAmount);

    HighRiskCodeResponse getHighRiskCodes();
}