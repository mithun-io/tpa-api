package com.tpa.service;

import com.tpa.dto.response.claim.ClaimDocumentResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {

    ClaimDocumentResponse uploadDocument(Long claimId, String documentType, MultipartFile multipartFile, String username);

    List<ClaimDocumentResponse> uploadDocuments(Long claimId, List<MultipartFile> multipartFiles, String username);

    ResponseEntity<Resource> downloadDocument(Long documentId, String username);

    ClaimDocumentResponse getDocument(Long documentId, String username);

    List<ClaimDocumentResponse> getDocumentsForClaim(Long claimId, String username);

    void validateFile(MultipartFile multipartFile);
}