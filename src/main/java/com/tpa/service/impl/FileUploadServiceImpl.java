package com.tpa.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.auth.DocumentValidationResponse;
import com.tpa.dto.response.claim.ClaimDocumentResponse;
import com.tpa.entity.Claim;
import com.tpa.entity.ClaimDocument;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.DocumentStatus;
import com.tpa.enums.DocumentType;
import com.tpa.enums.PolicyStatus;
import com.tpa.exception.BadRequestException;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.kafka.producer.ClaimEventProducer;
import com.tpa.mapper.ClaimDocumentMapper;
import com.tpa.repository.ClaimDocumentRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.service.AiClaimAssistantService;
import com.tpa.service.ClaimService;
import com.tpa.service.FileUploadService;
import com.tpa.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final ClaimDocumentRepository claimDocumentRepository;
    private final ClaimRepository claimRepository;

    private final StorageProvider storageProvider;

    private final RuleEngineService ruleEngineService;
    private final ClaimService claimService;
    private final ClaimEventProducer claimEventProducer;

    private final AiClaimAssistantService aiClaimAssistantService;

    private final ClaimDocumentMapper claimDocumentMapper;

    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ClaimDocumentResponse uploadDocument(Long claimId, String documentType, MultipartFile multipartFile) {
        validateFile(multipartFile);

        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("Claim not found"));
        String filePath = storageProvider.storeFile(multipartFile);

        ClaimDocument claimDocument = ClaimDocument.builder()
                .claim(claim)
                .fileName(multipartFile.getOriginalFilename())
                .filePath(filePath)
                .type(DocumentType.valueOf(documentType.toUpperCase()))
                .fileType(resolveFileType(multipartFile))
                .build();

        runAiValidation(claimDocument, claim, multipartFile, documentType);

        ClaimDocument savedDocument = claimDocumentRepository.save(claimDocument);
        claimRepository.save(claim);

        triggerRuleEngineIfEligible(claim);
        log.info("Document uploaded successfully for claim {}", claimId);

        return claimDocumentMapper.toResponse(savedDocument);
    }

    @Override
    @Transactional
    public List<ClaimDocumentResponse> uploadDocuments(Long claimId, List<MultipartFile> multipartFiles) {

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new BadRequestException("No files uploaded");
        }

        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("Claim not found"));

        List<ClaimDocument> savedDocuments = new ArrayList<>();

        boolean claimFormAssigned = false;

        for (MultipartFile multipartFile : multipartFiles) {
            validateFile(multipartFile);
            DocumentType documentType;

            if (!claimFormAssigned) {
                documentType = DocumentType.CLAIM_FORM;
                claimFormAssigned = true;
            } else {
                documentType = DocumentType.COMBINED_DOCUMENT;
            }

            String filePath = storageProvider.storeFile(multipartFile);

            ClaimDocument claimDocument = ClaimDocument.builder()
                    .claim(claim)
                    .fileName(multipartFile.getOriginalFilename())
                    .filePath(filePath)
                    .type(documentType)
                    .fileType(resolveFileType(multipartFile))
                    .build();

            runAiValidation(claimDocument, claim, multipartFile, documentType.name());

            savedDocuments.add(claimDocumentRepository.save(claimDocument));
        }

        claimRepository.save(claim);

        triggerRuleEngineIfEligible(claim);

        log.info("{} documents uploaded successfully for claim {}", savedDocuments.size(), claimId);
        return claimDocumentMapper.toResponses(savedDocuments);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadDocument(Long documentId) {
        ClaimDocument document = claimDocumentRepository.findById(documentId).orElseThrow(() -> new NoResourceFoundException("Document not found"));

        Resource resource = storageProvider.loadFileAsResource(document.getFilePath());

        return ResponseEntity.ok()
                .contentType(resolveMediaType(document))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimDocumentResponse getDocument(Long documentId) {
        ClaimDocument document = claimDocumentRepository.findById(documentId).orElseThrow(() -> new NoResourceFoundException("Document not found"));
        return claimDocumentMapper.toResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDocumentResponse> getDocumentsForClaim(Long claimId) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("Claim not found"));
        List<ClaimDocument> documents = claimDocumentRepository.findByClaim(claim);
        return claimDocumentMapper.toResponses(documents);
    }

    @Override
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        List<String> allowedTypes = List.of(
                "application/pdf",
                "image/jpeg",
                "image/png"
        );

        String contentType = file.getContentType();

        if (!allowedTypes.contains(contentType)) {throw new BadRequestException("Only PDF, JPG and PNG files are allowed");
        }
    }

    private String resolveFileType(MultipartFile file) {
        return file.getContentType() != null && file.getContentType().contains("pdf") ? "PDF" : "IMAGE";
    }

    private MediaType resolveMediaType(ClaimDocument document) {
        return "PDF".equalsIgnoreCase(document.getFileType()) ? MediaType.APPLICATION_PDF : MediaType.IMAGE_JPEG;
    }

    private void runAiValidation(ClaimDocument claimDocument, Claim claim, MultipartFile multipartFile, String documentType) {

        try {
            DocumentValidationResponse validationResponse = aiClaimAssistantService.validateDocument(multipartFile, documentType);

            claimDocument.setValidationStatus(DocumentStatus.valueOf(validationResponse.getStatus().name()));
            claimDocument.setConfidenceScore(validationResponse.getConfidenceScore());
            claimDocument.setValidationIssues(objectMapper.writeValueAsString(validationResponse.getIssues()));

            if (validationResponse.getIcdCode() != null && !validationResponse.getIcdCode().isBlank()) {
                claim.setIcdCode(validationResponse.getIcdCode());
            }

            if ("INVALID".equalsIgnoreCase(String.valueOf(validationResponse.getStatus()))) {
                claim.setClaimStatus(ClaimStatus.UNDER_REVIEW);
            }

        } catch (Exception e) {
            log.error("AI validation failed", e);
            claimDocument.setValidationStatus(DocumentStatus.valueOf("UNKNOWN"));
        }
    }

    private void triggerRuleEngineIfEligible(Claim claim) {
        List<ClaimDocument> claimDocuments = claimDocumentRepository.findByClaim(claim);

        boolean hasClaimForm = claimDocuments.stream().anyMatch(d -> d.getType() == DocumentType.CLAIM_FORM);
        boolean hasCombinedDocument = claimDocuments.stream().anyMatch(d -> d.getType() == DocumentType.COMBINED_DOCUMENT);

        if (!hasClaimForm || !hasCombinedDocument) {
            return;
        }

        ClaimRequest request = ClaimRequest.builder()
                .claimFormPresent(true)
                .combinedDocumentPresent(true)
                .policyNumber(claim.getPolicyNumber())
                .policyStatus(PolicyStatus.ACTIVE)
                .claimedAmount(claim.getAmount())
                .claimFormPatientName(claim.getPatientName())
                .combinedDocPatientName(claim.getPatientName())
                .claimFormHospitalName(claim.getHospitalName())
                .combinedDocHospitalName(claim.getHospitalName())
                .claimFormAdmissionDate(claim.getAdmissionDate())
                .combinedDocAdmissionDate(claim.getAdmissionDate())
                .claimFormDischargeDate(claim.getDischargeDate())
                .combinedDocDischargeDate(claim.getDischargeDate())
                .totalBillAmount(claim.getTotalBillAmount())
                .policyId(claim.getPolicyId())
                .carrierName(claim.getCarrierName())
                .policyName(claim.getPolicyName())
                .claimType(claim.getClaimType())
                .diagnosis(claim.getDiagnosis())
                .billNumber(claim.getBillNumber())
                .billDate(claim.getBillDate())
                .build();

        try {

            var decision = ruleEngineService.evaluateClaim(request);

            claimService.processClaimDecision(claim.getId(), decision);
            claimEventProducer.publishClaimCreatedEvent(claim.getId(), request);

            log.info("Rule engine executed for claim {}", claim.getId());

        } catch (Exception e) {
            log.error("Rule engine execution failed for claim {}", claim.getId(), e);
        }
    }
}