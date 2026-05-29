package com.tpa.controller;

import com.tpa.dto.request.claim.ClaimQueryRequest;
import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.claim.*;
import com.tpa.entity.ClaimAudit;
import com.tpa.enums.ClaimStatus;
import com.tpa.service.ClaimService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
@Tag(name = "Claims", description = "Insurance Claim Management APIs")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<ClaimResponse>> createClaim(@RequestBody ClaimRequest claimRequest) {
        ClaimResponse claimResponse = claimService.createClaim(claimRequest, currentUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim created successfully", claimResponse, 200));
    }

    @GetMapping("/{claimId}")
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST','CARRIER','PATIENT')")
    public ResponseEntity<ApiResponse<ClaimResponse>> getClaim(@PathVariable Long claimId) {
        ClaimResponse claimResponse = claimService.getClaim(claimId, currentUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim fetched successfully", claimResponse, 200));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST','CARRIER','PATIENT')")
    public ResponseEntity<ApiResponse<Page<ClaimResponse>>> getAllClaims(Pageable pageable) {
        Page<ClaimResponse> claimResponses = claimService.getAllClaims(pageable, currentUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claims fetched successfully", claimResponses, 200));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST','CARRIER','PATIENT')")
    public ResponseEntity<ApiResponse<Page<ClaimResponse>>> searchClaims(@RequestParam(required = false) ClaimStatus claimStatus,
                                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                                         @RequestParam(required = false) Double minAmount,
                                                                         @RequestParam(required = false) Double maxAmount,
                                                                         Pageable pageable) {
        Page<ClaimResponse> claimResponses = claimService.searchClaims(claimStatus, from, to, minAmount, maxAmount, currentUsername(), pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Claims search completed", claimResponses, 200));
    }

    @GetMapping("/{claimId}/audits")
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST','CARRIER','PATIENT')")
    public ResponseEntity<ApiResponse<List<ClaimAudit>>> getClaimAudits(@PathVariable Long claimId) {
        List<ClaimAudit> claimAudits = claimService.getClaimAudits(claimId, currentUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim audits fetched successfully", claimAudits, 200));
    }



    @GetMapping("/{claimId}/export")
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST','CARRIER','PATIENT')")
    public ResponseEntity<byte[]> exportClaimReport(@PathVariable Long claimId) {
        byte[] pdf = claimService.exportClaimReport(claimId, currentUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=claim-report-" + claimId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PutMapping("/{claimId}/carrier-approve")
    @PreAuthorize("hasRole('CARRIER')")
    public ResponseEntity<ApiResponse<Void>> carrierApproveClaim(@PathVariable Long claimId) {
        claimService.carrierApproveClaim(claimId, currentUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim approved successfully", null, 200));
    }

    @DeleteMapping("/{claimId}")
    @PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
    public ResponseEntity<ApiResponse<Void>> deleteClaim(@PathVariable Long claimId) {
        claimService.deleteClaim(claimId, currentUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim deleted successfully", null, 200));
    }

    @PostMapping("/bulk-approve")
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST')")
    public ResponseEntity<ApiResponse<BulkClaimProcessResponse>> processBulkApproval(@RequestBody List<Long> claimIds) {
        BulkClaimProcessResponse bulkClaimProcessResponse = claimService.processBulkApproval(claimIds, currentUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Bulk approval processed successfully", bulkClaimProcessResponse, 200));
    }

    @GetMapping("/{claimId}/queries")
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST','CARRIER','PATIENT')")
    public ResponseEntity<ApiResponse<List<ClaimQueryResponse>>> getClaimQueries(@PathVariable Long claimId) {
        List<ClaimQueryResponse> claimQueryResponses = claimService.getClaimQueries(claimId, currentUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim queries fetched successfully", claimQueryResponses, 200));
    }

    @PostMapping("/{claimId}/queries")
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST','CARRIER','PATIENT')")
    public ResponseEntity<ApiResponse<ClaimQueryResponse>> createClaimQuery(@PathVariable Long claimId, @RequestBody ClaimQueryRequest claimQueryRequest) {
        ClaimQueryResponse claimQueryResponsee = claimService.createClaimQuery(claimId, claimQueryRequest, currentUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim query created successfully", claimQueryResponsee, 200));
    }

    @PostMapping("/broadcast/{claimId}")
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST')")
    public ResponseEntity<ApiResponse<Void>> broadcastStatusUpdate(@PathVariable Long claimId, @RequestParam String status, @RequestParam(required = false) String message) {
        claimService.broadcastStatusUpdate(claimId, status, message);
        return ResponseEntity.ok(new ApiResponse<>(true, "Status broadcasted successfully", null, 200));
    }

    @PostMapping("/notify")
    @PreAuthorize("hasAnyRole('ADMIN','SPECIALIST')")
    public ResponseEntity<ApiResponse<Void>> sendUserNotification(@RequestParam String userEmail, @RequestParam String title, @RequestParam String message) {
        claimService.sendUserNotification(userEmail, title, message);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification sent successfully", null, 200));
    }
}