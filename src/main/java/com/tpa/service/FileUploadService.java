package com.tpa.service;

import com.tpa.dto.response.claim.ClaimDocumentResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {

    ClaimDocumentResponse uploadDocument(Long claimId, String documentType, MultipartFile multipartFile);

    List<ClaimDocumentResponse> uploadDocuments(Long claimId, List<MultipartFile> multipartFiles);

    ResponseEntity<Resource> downloadDocument(Long documentId);

    ClaimDocumentResponse getDocument(Long documentId);

    List<ClaimDocumentResponse> getDocumentsForClaim(Long claimId);

    void validateFile(MultipartFile multipartFile);
}