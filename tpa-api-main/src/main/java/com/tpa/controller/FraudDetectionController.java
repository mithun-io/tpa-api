package com.tpa.controller;

import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.auth.FraudDashboardResponse;
import com.tpa.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'CARRIER')")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FraudDashboardResponse>> getAdminDashboard() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin fraud dashboard fetched successfully", fraudDetectionService.getAdminFraudDashboard(), 200));
    }

    @GetMapping("/carrier/dashboard")
    @PreAuthorize("hasRole('CARRIER')")
    public ResponseEntity<ApiResponse<FraudDashboardResponse>> getCarrierDashboard(Authentication authentication) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Carrier fraud dashboard fetched successfully", fraudDetectionService.getCarrierFraudDashboard(authentication.getName()), 200));
    }

    @PatchMapping("/admin/claims/{id}/safe")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> markClaimAsSafe(@PathVariable Long id) {
        fraudDetectionService.markClaimAsSafe(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim marked as safe successfully", null, 200));
    }
}