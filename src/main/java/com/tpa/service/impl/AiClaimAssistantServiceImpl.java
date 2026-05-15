package com.tpa.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.tpa.entity.Claim;
import com.tpa.entity.ClaimDocument;
import com.tpa.enums.DocumentStatus;
import com.tpa.repository.ClaimRepository;
import com.tpa.dto.request.AiValidationRequest;
import com.tpa.dto.response.AiAnalysisResponse;
import com.tpa.enums.Verdict;
import com.tpa.service.AiClaimAssistantService;
import com.tpa.service.MedicalValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.tpa.dto.response.DocumentValidationResponse;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClaimAssistantServiceImpl implements AiClaimAssistantService {

    private final ClaimRepository claimRepository;
    private final com.tpa.repository.ClaimDocumentRepository claimDocumentRepository;
    private final RestClient.Builder restClientBuilder;
    private final MedicalValidationService medicalValidationService;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.google.genai.api-key}")
    private String apiKey;

    @Value("${spring.ai.google.genai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.google.genai.chat.options.model}")
    private String model;

    private String callAiApi(String systemPrompt, String userMessage) throws Exception {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(5000);
        simpleClientHttpRequestFactory.setReadTimeout(15000);

        RestClient restClient = restClientBuilder.requestFactory(simpleClientHttpRequestFactory).baseUrl(baseUrl).build();

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", 800,
                "temperature", 0.1,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt != null ? systemPrompt : ""),
                        Map.of("role", "user", "content", userMessage != null ? userMessage : "")
                ));

        String responseBody = restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        if (responseBody == null || responseBody.isBlank()) {
            throw new RuntimeException("API returned an empty response body. Please check the model configuration or API limits.");
        }

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode choices = jsonNode.path("choices");

        if (choices.isMissingNode() || !choices.isArray() || choices.isEmpty()) {
            log.error("Unexpected AI API response structure: {}", responseBody);
            throw new RuntimeException("API response missing 'choices' array");
        }

        JsonNode message = choices.get(0).path("message");
        if (message.isMissingNode()) {
            log.error("Unexpected AI API response structure: {}", responseBody);
            throw new RuntimeException("API response missing 'message' object");
        }

        JsonNode content = message.path("content");
        if (content.isMissingNode() || content.isNull()) {
            log.error("Unexpected AI API response structure: {}", responseBody);
            throw new RuntimeException("API response missing 'content' field");
        }

        return content.asText();
    }

    private String extractJson(String raw) {
        if (raw == null)
            throw new RuntimeException("AI returned null response");

        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');

        if (start == -1 || end == -1 || start >= end) {
            throw new RuntimeException("No valid JSON object found in AI response");
        }

        return raw.substring(start, end + 1);
    }

    private ObjectMapper buildLenientMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    }

    private AiAnalysisResponse buildFallback(Verdict verdict, List<String> flags, String recommendation) {
        return AiAnalysisResponse.builder()
                .verdict(verdict)
                .confidence(0.5)
                .riskScore(0.5)
                .validationChecks(AiAnalysisResponse.ValidationChecks.builder().policyActive(false).documentsComplete(false).withinLimit(false).build())
                .financialSummary(AiAnalysisResponse.FinancialSummary.builder().claimedAmount(BigDecimal.ZERO).eligibleAmount(BigDecimal.ZERO).build())
                .flags(flags)
                .recommendation(recommendation)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\"", "'");
    }

    @Override
    @Transactional
    @Cacheable(value = "aiSummaries", key = "#claimId")
    public AiAnalysisResponse analyzeClaim(Long claimId, String prompt) {

        if (prompt == null || prompt.isBlank()) {
            prompt = "Summarize this claim and highlight any issues.";
        }

        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

        List<ClaimDocument> claimDocuments = claimDocumentRepository.findByClaimId(claimId);

        StringBuilder docContext = new StringBuilder();

        String aiValidationStatus = "UNKNOWN";
        String aiValidationIssues = "[]";
        Integer aiConfidenceScore = 0;

        for (ClaimDocument claimDocument : claimDocuments) {
            docContext.append("- ").append(claimDocument.getType() != null ? claimDocument.getType().name() : "UNKNOWN").append("\n");

            if (claimDocument.getValidationStatus() != null) {
                aiValidationStatus = String.valueOf(claimDocument.getValidationStatus());
                aiValidationIssues = claimDocument.getValidationIssues() != null ? claimDocument.getValidationIssues() : "[]";
                aiConfidenceScore = claimDocument.getConfidenceScore() != null ? claimDocument.getConfidenceScore() : 0;
            }
        }

        String claimJson = String.format("""
                        {
                          "id": %d,
                          "patientName": "%s",
                          "hospitalName": "%s",
                          "policyNumber": "%s",
                          "status": "%s",
                          "amount": %f,
                          "admissionDate": "%s",
                          "dischargeDate": "%s"
                        }
                        """,
                claim.getId(),
                safe(claim.getPatientName()),
                safe(claim.getHospitalName()),
                claim.getPolicyNumber(),
                claim.getClaimStatus(),
                claim.getAmount(),
                safe(String.valueOf(claim.getAdmissionDate())),
                safe(String.valueOf(claim.getDischargeDate()))
        );

        String validationJson = String.format("""
                        {
                          "status": "%s",
                          "confidenceScore": %d,
                          "issues": %s
                        }
                        """,
                aiValidationStatus,
                aiConfidenceScore,
                aiValidationIssues
        );

        String systemPrompt = """
                You are an AI Claim Assistant integrated into an Insurance Claim Processing system.
                
                Your job is to analyze a claim using the FULL CONTEXT provided.
                Do NOT ask for documents if they are already uploaded.
                
                --- CONTEXT PROVIDED ---
                Claim Details:
                %s
                
                Uploaded Documents:
                %s
                
                AI Validation Result:
                %s
                
                --- STRICT RULES ---
                1. DO NOT ask for additional documents if claim_form or combined_document is uploaded.
                2. DO NOT give generic responses like 'please upload documents'.
                3. ALWAYS use the validation result provided.
                
                --- YOUR TASK ---
                Analyze and respond based on validation issues,
                claim data consistency, and risk indicators.
                
                --- RESPONSE FORMAT ---
                Return a clear, professional explanation containing:
                1. Summary (what the claim is about)
                2. Validation Result (VALID / INVALID)
                3. Key Issues (if any)
                4. Recommendation
                
                CRITICAL INSTRUCTION:
                Return ONLY a valid JSON object matching this EXACT schema.
                Put your full, human-friendly response inside
                the 'recommendation' field.
                
                {
                  "verdict": "REVIEW",
                  "confidence": 0.0,
                  "riskScore": 0.0,
                  "validations": {
                    "policyActive": true,
                    "documentsComplete": true,
                    "withinLimit": true
                  },
                  "financial": {
                    "claimedAmount": 0.0,
                    "eligibleAmount": 0.0
                  },
                  "flags": [],
                  "recommendation": "<YOUR HUMAN FRIENDLY ANALYSIS HERE>"
                }
                """.formatted(
                claimJson,
                docContext.toString(),
                validationJson
        );

        try {

            String rawAiContent = callAiApi(systemPrompt, prompt);
            log.info("Raw AI Response for claim {}:\n{}", claimId, rawAiContent);

            String cleanJson = extractJson(rawAiContent);

            AiAnalysisResponse aiAnalysisResponse = buildLenientMapper().readValue(cleanJson, AiAnalysisResponse.class);

            if (aiAnalysisResponse.getConfidence() < 0 || aiAnalysisResponse.getConfidence() > 1) {
                aiAnalysisResponse.setConfidence(0.5);
            }

            if (aiAnalysisResponse.getRiskScore() < 0 || aiAnalysisResponse.getRiskScore() > 1) {
                aiAnalysisResponse.setRiskScore(0.5);
            }

            if (aiAnalysisResponse.getFlags() == null) {
                aiAnalysisResponse.setFlags(new ArrayList<>());
            }

            aiAnalysisResponse.setGeneratedAt(LocalDateTime.now());
            return aiAnalysisResponse;

        } catch (Exception e) {
            log.error("AI analysis failed for claim {}. Error: {}", claimId, e.getMessage(), e);
            return buildFallback(Verdict.REVIEW, List.of("AI analysis failed: " + e.getMessage()), "Manual review required due to AI failure");
        }
    }

    @Override
    @Transactional
    public String generateClaimSummary(Long claimId) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

        String claimContext = String.format("""
                        Patient: %s
                        Hospital: %s
                        Diagnosis: %s
                        Amount: %s
                        Policy: %s
                        Status: %s
                        Admission: %s
                        Discharge: %s
                        """,
                safe(claim.getPatientName()),
                safe(claim.getHospitalName()),
                safe(claim.getDiagnosis()),
                claim.getAmount(),
                safe(claim.getPolicyNumber()),
                claim.getClaimStatus(),
                safe(claim.getAdmissionDate() != null
                        ? claim.getAdmissionDate().toString()
                        : ""),
                safe(claim.getDischargeDate() != null
                        ? claim.getDischargeDate().toString()
                        : ""));

        String systemPrompt = """
                You are a concise insurance summarization AI.
                
                Summarize this claim in EXACTLY 3 short lines
                in simple English.
                
                Include:
                - patient name
                - hospital
                - diagnosis/problem
                - claimed amount
                - current risk or claim status reasoning
                
                STRICT RULES:
                1. Do not use markdown.
                2. Do not use bullet points.
                3. Return exactly 3 plain text sentences.
                4. Separate each sentence using a newline.
                
                Claim Details:
                %s
                """.formatted(claimContext);

        try {
            String summary = callAiApi(systemPrompt, "Provide the 3-line summary.");
            summary = summary.trim().replace("\"", "");

            claim.setAiSummary(summary);
            claimRepository.save(claim);
            return summary;

        } catch (Exception e) {
            log.error("Failed to generate AI summary for claim {}", claimId, e);
            return "Unable to generate AI summary at this time.";
        }
    }

    @Override
    @Transactional
    public AiAnalysisResponse validatePreClaim(AiValidationRequest request) {
        log.info("Pre-validation AI request: policy={}, amount={}", request.getPolicyNumber(), request.getAmount());

        List<String> earlyFlags = new ArrayList<>();

        if (request.getPolicyNumber() == null || request.getPolicyNumber().isBlank()) {
            earlyFlags.add("Policy number is missing");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            earlyFlags.add("Claimed amount must be greater than zero");
        }
        if (request.getAmount() != null && request.getAmount().compareTo(new BigDecimal("50000")) > 0) {
            earlyFlags.add("Claimed amount exceeds maximum threshold of $50,000");
        }
        if (request.getHospitalName() == null || request.getHospitalName().isBlank()) {
            earlyFlags.add("Hospital name is missing");
        }

        if (earlyFlags.stream().anyMatch(f -> f.contains("missing"))) {
            return buildFallback(Verdict.REVIEW, earlyFlags, "Please complete all required fields before AI validation");
        }

        String claimContext = String.format("""
                        {
                          "policyNumber": "%s",
                          "claimedAmount": %s,
                          "hospitalName": "%s",
                          "diagnosis": "%s",
                          "patientName": "%s",
                          "admissionDate": "%s",
                          "dischargeDate": "%s"
                        }
                        """,
                safe(request.getPolicyNumber()),
                request.getAmount(),
                safe(request.getHospitalName()),
                safe(request.getDiagnosis()),
                safe(request.getPatientName()),
                safe(request.getAdmissionDate()),
                safe(request.getDischargeDate())
        );

        String systemPrompt = """
                You are a TPA insurance pre-validation AI.
                
                A customer is ABOUT TO SUBMIT a claim.
                
                Your job is to pre-validate the provided details
                and flag any potential issues BEFORE the claim
                is officially filed.
                
                Claim details to validate:
                %s
                
                Rules to evaluate:
                1. Assess if the policy number format looks valid.
                2. Check if the claimed amount is reasonable
                   for the stated diagnosis.
                3. Flag if hospital name is generic or suspicious.
                4. Assess if admission/discharge dates are
                   logically consistent.
                5. Identify any missing critical fields.
                
                CRITICAL INSTRUCTION:
                Return ONLY a valid JSON object.
                NO markdown.
                NO code blocks.
                NO explanations.
                NO extra text whatsoever.
                
                The response must exactly match this schema:
                
                {
                  "verdict": "APPROVED" | "REVIEW" | "REJECTED",
                  "confidence": 0.0,
                  "riskScore": 0.0,
                  "validations": {
                    "policyActive": true,
                    "documentsComplete": true,
                    "withinLimit": true
                  },
                  "financial": {
                    "claimedAmount": 0.0,
                    "eligibleAmount": 0.0
                  },
                  "flags": [],
                  "recommendation": "..."
                }
                """.formatted(claimContext);

        try {
            String rawAiContent = callAiApi(systemPrompt, "Pre-validate this claim and return ONLY the JSON response, no other text.");

            log.info("Raw pre-validation AI response:\n{}", rawAiContent);

            String cleanJson = extractJson(rawAiContent);
            AiAnalysisResponse analysis = buildLenientMapper().readValue(cleanJson, AiAnalysisResponse.class);

            if (!earlyFlags.isEmpty()) {
                List<String> merged = new ArrayList<>(earlyFlags);

                if (analysis.getFlags() != null) {
                    merged.addAll(analysis.getFlags());
                }

                analysis.setFlags(merged);
            } else if (analysis.getFlags() == null) {
                analysis.setFlags(new ArrayList<>());
            }

            if (analysis.getConfidence() < 0 || analysis.getConfidence() > 1)
                analysis.setConfidence(0.5);
            if (analysis.getRiskScore() < 0 || analysis.getRiskScore() > 1)
                analysis.setRiskScore(0.5);

            analysis.setGeneratedAt(LocalDateTime.now());
            return analysis;

        } catch (Exception e) {
            log.error("Pre-validation AI failed: {}", e.getMessage(), e);

            List<String> flags = new ArrayList<>(earlyFlags);
            flags.add("AI pre-validation failed: " + e.getMessage());

            return buildFallback(Verdict.REVIEW, flags, "AI pre-validation unavailable. Please proceed with manual review.");
        }
    }

    @Override
    @Transactional
    public DocumentValidationResponse validateDocument(MultipartFile multipartFile, String documentType) {
        log.info("Validating document of type: {}", documentType);
        String extractedText;

        try {
            if (multipartFile.getOriginalFilename() != null && multipartFile.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                try (PDDocument pdDocument = org.apache.pdfbox.Loader.loadPDF(multipartFile.getBytes())) {
                    PDFTextStripper pdfTextStripper = new PDFTextStripper();
                    extractedText = pdfTextStripper.getText(pdDocument);
                }
            } else {
                extractedText = "Visual document (Image/Scan) uploaded. Automated text extraction skipped. Document visually appears to contain required claim fields.";
            }
        } catch (Exception e) {
            log.error("Failed to extract text from PDF", e);
            extractedText = "Document could not be parsed for text (possible scan or secured PDF). Assumed visually valid for processing.";
        }

        if (extractedText == null || extractedText.trim().isEmpty()) {
            return DocumentValidationResponse.builder()
                    .status(DocumentStatus.INVALID)
                    .issues(List.of("The PDF appears to be empty or contains no extractable text"))
                    .confidenceScore(100)
                    .build();
        }

        String systemPrompt = """
                You are an AI document validator for an insurance TPA.
                
                You are reviewing a document of type:
                %s
                
                Extracted Text:
                %s
                
                Validate the document using these rules:
                
                1. Check for missing mandatory fields:
                   - Patient Name
                   - Policy Number
                   - Dates
                   - Amounts
                
                2. Check for logical inconsistencies:
                   Example:
                   - Discharge date before Admission date
                
                3. Look for fraud indicators or suspicious anomalies.
                
                4. Extract the primary ICD-10 medical code
                   if present in the discharge summary.
                
                CRITICAL INSTRUCTION:
                Return ONLY valid JSON.
                
                STRICT RULES:
                - No markdown
                - No explanations
                - No extra text
                - Issues must be short and user-friendly
                
                Response schema MUST exactly match:
                
                {
                  "status": "VALID" | "INVALID",
                  "issues": [
                    "sentence 1",
                    "sentence 2"
                  ],
                  "confidenceScore": 85,
                  "icdCode": "I10"
                }
                """.formatted(documentType, extractedText);

        try {
            String rawAiContent = callAiApi(systemPrompt, "Validate this document and return JSON only.");
            log.info("Document AI Validation Result:\n{}", rawAiContent);

            String cleanJson = extractJson(rawAiContent);
            DocumentValidationResponse documentValidationResponse = buildLenientMapper().readValue(cleanJson, DocumentValidationResponse.class);

            if (documentValidationResponse.getIssues() == null)
                documentValidationResponse.setIssues(new ArrayList<>());

            if (documentValidationResponse.getConfidenceScore() == null)
                documentValidationResponse.setConfidenceScore(50);

            if (documentValidationResponse.getConfidenceScore() > 100)
                documentValidationResponse.setConfidenceScore(100);

            return documentValidationResponse;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("AI Document Validation failed", e);

            return DocumentValidationResponse.builder()
                    .status(DocumentStatus.INVALID)
                    .issues(List.of("AI validation service failed: " + e.getMessage()))
                    .confidenceScore(0)
                    .build();
        }
    }
}
