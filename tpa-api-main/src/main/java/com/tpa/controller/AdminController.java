package com.tpa.controller;

import com.tpa.dto.request.claim.ClaimReviewRequest;
import com.tpa.dto.response.analytics.AiAnalysisResponse;
import com.tpa.dto.response.analytics.MonitoringResponse;
import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.claim.ClaimResponse;
import com.tpa.dto.response.user.CarrierResponse;
import com.tpa.dto.response.user.UserResponse;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.UserStatus;
import com.tpa.service.AdminService;
import com.tpa.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ClaimService claimService;

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse<UserResponse>> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User blocked successfully", adminService.blockUser(id), 200));
    }

    @PatchMapping("/users/{id}/unblock")
    public ResponseEntity<ApiResponse<UserResponse>> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User unblocked successfully", adminService.unblockUser(id), 200));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Users fetched successfully", adminService.getAllUsers(page, size, search), 200));
    }

    @GetMapping("/carriers")
    public ResponseEntity<ApiResponse<Page<CarrierResponse>>> getAllCarriers(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) UserStatus userStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "true") boolean desc) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Carriers fetched successfully", adminService.getAllCarriers(companyName, userStatus, page, size, sortBy, desc), 200));
    }

    @GetMapping("/patients")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllPatients(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserStatus userStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "true") boolean desc) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Patients fetched successfully", adminService.getAllPatients(username, email, userStatus, page, size, sortBy, desc), 200));
    }

    @GetMapping("/claims")
    public ResponseEntity<ApiResponse<Page<ClaimResponse>>> getAllClaims(
            @RequestParam(required = false) ClaimStatus claimStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAt,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "true") boolean desc) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claims fetched successfully", adminService.getAllClaims(claimStatus, createdAt, page, size, sortBy, desc), 200));
    }

    @PatchMapping("/claims/review")
    public ResponseEntity<ApiResponse<ClaimResponse>> reviewClaim(@Valid @RequestBody ClaimReviewRequest claimReviewRequest, Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim reviewed successfully", adminService.reviewClaim(claimReviewRequest, principal), 200));
    }

    @PatchMapping("/claims/{id}/approve")
    public ResponseEntity<ApiResponse<ClaimResponse>> approveClaim(@PathVariable Long id, @RequestParam(required = false) String reason, Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim approved successfully", adminService.approveClaim(id, reason != null ? reason : "Approved by admin", principal), 200));
    }

    @PatchMapping("/claims/{id}/reject")
    public ResponseEntity<ApiResponse<ClaimResponse>> rejectClaim(@PathVariable Long id, @RequestParam String reason, Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim rejected successfully", adminService.rejectClaim(id, reason, principal), 200));
    }

    @PatchMapping("/carriers/{id}/approve")
    public ResponseEntity<ApiResponse<CarrierResponse>> approveCarrier(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Carrier approved successfully", adminService.approveCarrier(id), 200));
    }

    @PatchMapping("/carriers/{id}/reject")
    public ResponseEntity<ApiResponse<CarrierResponse>> rejectCarrier(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Carrier rejected successfully", adminService.rejectCarrier(id), 200));
    }

    @PatchMapping("/claims/{id}/assign-carrier")
    public ResponseEntity<ApiResponse<ClaimResponse>> assignClaimToCarrier(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Carrier assigned successfully", adminService.assignClaimToCarrier(id, body.get("carrierId")), 200));
    }

    @GetMapping("/claims/{id}/ai-summary")
    public ResponseEntity<ApiResponse<AiAnalysisResponse>> getClaimAiSummary(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Ai summary fetched successfully", adminService.getClaimAiSummary(id), 200));
    }

    @PostMapping("/claims/{id}/ai-chat")
    public ResponseEntity<ApiResponse<AiAnalysisResponse>> askAiAboutClaim(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Ai analysis fetched successfully", adminService.askAiAboutClaim(id, request.getOrDefault("prompt", "Analyze this claim")), 200));
    }

    @GetMapping("/monitoring")
    public ResponseEntity<ApiResponse<MonitoringResponse>> getSystemMonitoring() {
        return ResponseEntity.ok(new ApiResponse<>(true, "system monitoring fetched successfully", adminService.getSystemMonitoring(), 200));
    }
}
