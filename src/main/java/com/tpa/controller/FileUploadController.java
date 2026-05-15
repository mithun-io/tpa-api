package com.tpa.controller;

import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.claim.ClaimDocumentResponse;
import com.tpa.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<ClaimDocumentResponse>> uploadDocument(@RequestParam Long claimId, @RequestParam MultipartFile file, @RequestParam(defaultValue = "SUPPORTING_DOCUMENT") String documentType) {
        return ResponseEntity.ok(new ApiResponse<>(true, "File uploaded successfully", fileUploadService.uploadDocument(claimId, documentType, file), 200));
    }

    @PostMapping(value = "/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<ClaimDocumentResponse>>> uploadDocuments(@RequestParam Long claimId, @RequestParam List<MultipartFile> files) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Files uploaded successfully", fileUploadService.uploadDocuments(claimId, files), 200));
    }

    @GetMapping("/download/{documentId}")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','SPECIALIST')")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        return fileUploadService.downloadDocument(documentId);
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','SPECIALIST')")
    public ResponseEntity<ApiResponse<ClaimDocumentResponse>> getDocument(@PathVariable Long documentId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Document fetched successfully", fileUploadService.getDocument(documentId), 200));
    }

    @GetMapping("/claim/{claimId}")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','SPECIALIST')")
    public ResponseEntity<ApiResponse<List<ClaimDocumentResponse>>> getDocumentsForClaim(@PathVariable Long claimId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Documents fetched successfully", fileUploadService.getDocumentsForClaim(claimId), 200));
    }
}