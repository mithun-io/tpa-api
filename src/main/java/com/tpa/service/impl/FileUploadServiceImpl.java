package com.tpa.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.claim.ClaimDocumentResponse;
import com.tpa.entity.Claim;
import com.tpa.entity.ClaimDocument;
import com.tpa.enums.DocumentStatus;
import com.tpa.enums.DocumentType;
import com.tpa.enums.PolicyStatus;
import com.tpa.enums.UserRole;
import com.tpa.entity.User;
import com.tpa.exception.BadRequestException;
import com.tpa.exception.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;
import com.tpa.kafka.producer.ClaimEventProducer;
import com.tpa.mapper.ClaimDocumentMapper;
import com.tpa.repository.ClaimDocumentRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.UserRepository;
import com.tpa.service.ClaimService;
import com.tpa.service.FileUploadService;
import com.tpa.service.RuleEngineService;
import com.tpa.helper.StorageProvider;
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
    private final UserRepository userRepository;

    private final StorageProvider storageProvider;

    private final RuleEngineService ruleEngineService;
    private final ClaimService claimService;
    private final ClaimEventProducer claimEventProducer;
    private final ClaimDocumentMapper claimDocumentMapper;
    private final ObjectMapper objectMapper;

    private void verifyOwnership(Claim claim, String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NoResourceFoundException("User not found"));
        if (user.getUserRole() == UserRole.ADMIN || user.getUserRole() == UserRole.SPECIALIST) {
            return;
        }
        if (user.getUserRole() == UserRole.PATIENT && !claim.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have access to this claim");
        }
        if (user.getUserRole() == UserRole.CARRIER && (claim.getCarrier() == null || !claim.getCarrier().getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You don't have access to this claim");
        }
    }

    @Override
    public ClaimDocumentResponse uploadDocument(Long claimId, String documentType, MultipartFile multipartFile, String username) {
        validateFile(multipartFile);

        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("Claim not found"));
        verifyOwnership(claim, username);
        String filePath = storageProvider.storeFile(multipartFile);

        ClaimDocument claimDocument = ClaimDocument.builder()
                .claim(claim)
                .fileName(multipartFile.getOriginalFilename())
                .filePath(filePath)
                .type(DocumentType.valueOf(documentType.toUpperCase()))
                .fileType(resolveFileType(multipartFile))
                .build();

        applyDocumentDefaults(claimDocument);

        ClaimDocument savedDocument = claimDocumentRepository.save(claimDocument);
        claimRepository.save(claim);

        triggerRuleEngineIfEligible(claim);
        log.info("Document uploaded successfully for claim {}", claimId);

        return claimDocumentMapper.toResponse(savedDocument);
    }

    @Override
    public List<ClaimDocumentResponse> uploadDocuments(Long claimId, List<MultipartFile> multipartFiles, String username) {

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new BadRequestException("No files uploaded");
        }

        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("Claim not found"));
        verifyOwnership(claim, username);

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

            applyDocumentDefaults(claimDocument);

            savedDocuments.add(claimDocumentRepository.save(claimDocument));
        }

        claimRepository.save(claim);

        triggerRuleEngineIfEligible(claim);

        log.info("{} documents uploaded successfully for claim {}", savedDocuments.size(), claimId);
        return claimDocumentMapper.toResponses(savedDocuments);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadDocument(Long documentId, String username) {
        ClaimDocument document = claimDocumentRepository.findById(documentId).orElseThrow(() -> new NoResourceFoundException("Document not found"));
        verifyOwnership(document.getClaim(), username);

        Resource resource = storageProvider.loadFileAsResource(document.getFilePath());

        return ResponseEntity.ok()
                .contentType(resolveMediaType(document))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimDocumentResponse getDocument(Long documentId, String username) {
        ClaimDocument document = claimDocumentRepository.findById(documentId).orElseThrow(() -> new NoResourceFoundException("Document not found"));
        verifyOwnership(document.getClaim(), username);
        return claimDocumentMapper.toResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDocumentResponse> getDocumentsForClaim(Long claimId, String username) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("Claim not found"));
        verifyOwnership(claim, username);
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

    private void applyDocumentDefaults(ClaimDocument claimDocument) {
        claimDocument.setValidationStatus(DocumentStatus.VALID);
        claimDocument.setConfidenceScore(1.0);
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