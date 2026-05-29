package com.tpa.controller;

import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.claim.CarrierClaimDetailResponse;
import com.tpa.dto.response.claim.PolicyStatusResponse;
import com.tpa.service.CarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/carrier")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CARRIER')")
public class CarrierController {

    private final CarrierService carrierService;

    private String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        return authentication.getName();
    }

    @GetMapping("/claims")
    public ResponseEntity<ApiResponse<List<CarrierClaimDetailResponse>>> getAssignedClaims() {
        return ResponseEntity.ok(new ApiResponse<>(true, "claims fetched successfully", carrierService.getAssignedClaims(currentUser()), 200));
    }

    @GetMapping("/claims/{id}")
    public ResponseEntity<ApiResponse<CarrierClaimDetailResponse>> getClaimDetail(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "claim detail fetched successfully", carrierService.getClaimDetail(id, currentUser()), 200));
    }

    @PatchMapping("/claims/{id}/validate")
    public ResponseEntity<ApiResponse<Void>> validatePolicy(@PathVariable Long id) {
        carrierService.validatePolicy(id, currentUser());
        return ResponseEntity.ok(new ApiResponse<>(true, "policy validated successfully", null, 200));
    }

    @PatchMapping("/claims/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveClaim(@PathVariable Long id) {
        carrierService.approveClaim(id, currentUser());
        return ResponseEntity.ok(new ApiResponse<>(true, "claim approved successfully", null, 200));
    }

    @PatchMapping("/claims/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectClaim(@PathVariable Long id) {
        carrierService.rejectClaim(id, currentUser());
        return ResponseEntity.ok(new ApiResponse<>(true, "claim rejected successfully", null, 200));
    }

    @PatchMapping("/claims/{id}/remark")
    public ResponseEntity<ApiResponse<Void>> addRemark(@PathVariable Long id, @RequestBody Map<String, String> body) {
        carrierService.addRemark(id, body.get("remark"), currentUser());
        return ResponseEntity.ok(new ApiResponse<>(true, "remark added successfully", null, 200));
    }

    @PatchMapping("/claims/{id}/flag")
    public ResponseEntity<ApiResponse<Void>> flagSuspicious(@PathVariable Long id) {
        carrierService.flagSuspicious(id, currentUser());
        return ResponseEntity.ok(new ApiResponse<>(true, "claim flagged successfully", null, 200));
    }

    @GetMapping("/claims/{id}/policy-status")
    public ResponseEntity<ApiResponse<PolicyStatusResponse>> getPolicyStatus(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "policy status fetched successfully", carrierService.getPolicyStatus(id, currentUser()), 200));
    }
}
