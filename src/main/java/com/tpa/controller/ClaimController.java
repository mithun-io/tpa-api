package com.tpa.controller;

import com.tpa.dto.request.ClaimDataRequest;
import com.tpa.dto.response.ApiResponse;
import com.tpa.dto.response.ClaimResponse;
import com.tpa.entity.ClaimAudit;
import com.tpa.enums.ClaimStatus;
import com.tpa.service.ClaimService;
import com.tpa.helper.PdfExportService;
import com.tpa.service.CarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
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
    private final PdfExportService pdfExportService;
    private final CarrierService carrierService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ClaimResponse> createClaim(@RequestBody ClaimDataRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(claimService.createClaim(request, username));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'FMG_EMPLOYEE', 'CARRIER_USER', 'CUSTOMER')")
    public ResponseEntity<ClaimResponse> getClaim(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ClaimResponse claimResponse = claimService.getClaim(id);
        if (claimResponse == null) {
            return ResponseEntity.notFound().build();
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            if (claimResponse.getUserEmail() == null || !claimResponse.getUserEmail().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(claimResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ClaimResponse>> searchClaims(
            @RequestParam(required = false) ClaimStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String username,
            Pageable pageable) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isCustomer = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));

        if (isCustomer) {
            username = currentUsername;
        }

        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdDate")
            );
        }
        return ResponseEntity.ok(claimService.searchClaims(status, from, to, minAmount, maxAmount, username, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<ClaimResponse>> getAllClaims(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_FMG_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(claimService.getAllClaims(pageable));
        } else {
            return ResponseEntity.ok(claimService.searchClaims(null, null, null, null, null, authentication.getName(), pageable));
        }
    }

    @GetMapping("/{id}/export")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'FMG_EMPLOYEE', 'CARRIER_USER', 'CUSTOMER')")
    public ResponseEntity<byte[]> exportClaimReport(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ClaimResponse claimResponse = claimService.getClaim(id);

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            if (claimResponse.getUserEmail() == null || !claimResponse.getUserEmail().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        byte[] pdfBytes = pdfExportService.exportClaimReport(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"claim-report-" + id + ".pdf\"");
        httpHeaders.setContentLength(pdfBytes.length);
        return ResponseEntity.ok().headers(httpHeaders).body(pdfBytes);
    }

    @GetMapping("/{id}/audits")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'FMG_EMPLOYEE', 'CARRIER_USER', 'CUSTOMER')")
    public ResponseEntity<List<ClaimAudit>> getClaimAudits(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ClaimResponse claimResponse = claimService.getClaim(id);
        if (claimResponse == null) {
            return ResponseEntity.notFound().build();
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            if (claimResponse.getUserEmail() == null || !claimResponse.getUserEmail().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(claimService.getClaimAudits(id));
    }

    @GetMapping("/{id}/timeline")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'FMG_EMPLOYEE', 'CARRIER_USER', 'CUSTOMER')")
    public ResponseEntity<List<ClaimAudit>> getClaimTimeline(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ClaimResponse claimResponse = claimService.getClaim(id);
        if (claimResponse == null) {
            return ResponseEntity.notFound().build();
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            if (claimResponse.getUserEmail() == null || !claimResponse.getUserEmail().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(claimService.getClaimAudits(id));
    }

    @PutMapping("/{id}/carrier-approve")
    @PreAuthorize("hasRole('CARRIER_USER')")
    public ResponseEntity<ApiResponse<Void>> carrierApproveClaim(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        carrierService.approveClaim(id, authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim status updated to CARRIER_APPROVED", null, 200));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FMG_ADMIN', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deleteClaim(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        claimService.deleteClaim(id, authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim deleted successfully", null, 200));
    }
}
