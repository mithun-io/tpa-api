package com.tpa.controller;

import com.tpa.entity.ClaimDocument;
import com.tpa.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final FileUploadService fileUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("claimId") Long claimId
    ) {
        log.info("Received upload request - File: {}, Content-Type: {}, ClaimID: {}", 
                 file.getOriginalFilename(), file.getContentType(), claimId);

        try {
            // Correct Validation Rule: PDF OR JPG/PNG
            String contentType = file.getContentType();
            boolean isPdf = "application/pdf".equals(contentType);
            boolean isImage = "image/jpeg".equals(contentType) || "image/png".equals(contentType);

            if (!isPdf && !isImage) {
                log.warn("Rejected file: {}. Invalid type: {}", file.getOriginalFilename(), contentType);
                Map<String, String> error = new HashMap<>();
                error.put("message", "Please upload a valid file (PDF or Image)");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            log.info("File validated successfully. Proceeding with upload.");
            ClaimDocument doc = fileUploadService.uploadFile(claimId, "SUPPORTING_DOCUMENT", file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "File uploaded successfully");
            String fileName = new java.io.File(doc.getFilePath()).getName();
            response.put("fileUrl", "/uploads/" + fileName);
            response.put("documentId", doc.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Upload failed for file {}: {}", file.getOriginalFilename(), e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
