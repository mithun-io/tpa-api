package com.tpa.controller;

import com.tpa.dto.request.ClaimRequest;
import com.tpa.dto.response.ApiResponse;
import com.tpa.dto.response.ClaimResponse;
import com.tpa.entity.ClaimAudit;
import com.tpa.enums.ClaimStatus;
import com.tpa.service.ClaimService;
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
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    private String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ClaimResponse>> createClaim(@RequestBody ClaimRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim created successfully", claimService.createClaim(request, currentUser()), 200));
    }

    @GetMapping("/{id}")
    @PreAuthorize("""
            hasAnyRole(
                'FMG_ADMIN',
                'FMG_EMPLOYEE',
                'CARRIER_USER',
                'CUSTOMER'
            )
            """)
    public ResponseEntity<ApiResponse<ClaimResponse>> getClaim(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim fetched successfully", claimService.getClaim(id, currentUser()), 200));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ClaimResponse>>> searchClaims(@RequestParam(required = false) ClaimStatus status,
                                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                                         @RequestParam(required = false) Double minAmount,
                                                                         @RequestParam(required = false) Double maxAmount,
                                                                         @RequestParam(required = false) String username,
                                                                         Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claims fetched successfully", claimService.searchClaims(status, from, to, minAmount, maxAmount, username, pageable), 200));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClaimResponse>>> getAllClaims(Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claims fetched successfully", claimService.getAllClaims(pageable, currentUser()), 200));
    }

    @GetMapping("/{id}/export")
    @PreAuthorize("""
            hasAnyRole(
                'FMG_ADMIN',
                'FMG_EMPLOYEE',
                'CARRIER_USER',
                'CUSTOMER'
            )
            """)
    public ResponseEntity<byte[]> exportClaimReport(@PathVariable Long id) {
        byte[] pdfBytes = claimService.exportClaimReport(id, currentUser());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"claim-report-" + id + ".pdf\"");
        headers.setContentLength(pdfBytes.length);
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/{id}/audits")
    @PreAuthorize("""
            hasAnyRole(
                'FMG_ADMIN',
                'FMG_EMPLOYEE',
                'CARRIER_USER',
                'CUSTOMER'
            )
            """)
    public ResponseEntity<ApiResponse<List<ClaimAudit>>> getClaimAudits(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim audits fetched successfully", claimService.getClaimAudits(id, currentUser()), 200));
    }

    @GetMapping("/{id}/timeline")
    @PreAuthorize("""
            hasAnyRole(
                'FMG_ADMIN',
                'FMG_EMPLOYEE',
                'CARRIER_USER',
                'CUSTOMER'
            )
            """)
    public ResponseEntity<ApiResponse<List<ClaimAudit>>> getClaimTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim timeline fetched successfully", claimService.getClaimTimeline(id, currentUser()), 200));
    }

    @PutMapping("/{id}/carrier-approve")
    @PreAuthorize("hasRole('CARRIER_USER')")
    public ResponseEntity<ApiResponse<Void>> carrierApproveClaim(@PathVariable Long id) {
        claimService.carrierApproveClaim(id, currentUser());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim status updated to carrier approved", null, 200));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("""
            hasAnyRole(
                'FMG_ADMIN',
                'CUSTOMER'
            )
            """)
    public ResponseEntity<ApiResponse<Void>> deleteClaim(@PathVariable Long id) {
        claimService.deleteClaim(id, currentUser());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim deleted successfully", null, 200));
    }
}