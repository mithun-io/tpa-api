package com.tpa.controller;

import com.tpa.dto.request.ClaimReviewRequest;
import com.tpa.dto.response.CarrierResponse;
import com.tpa.dto.response.ClaimResponse;
import com.tpa.dto.response.UserResponse;
import com.tpa.dto.response.AiAnalysisResponse;
import com.tpa.enums.ClaimStatus;
import com.tpa.service.AdminService;
import com.tpa.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FMG_ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ClaimService claimService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminService.getAllUsers(page, size, search));
    }

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<UserResponse> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.blockUser(id));
    }

    @PatchMapping("/users/{id}/unblock")
    public ResponseEntity<UserResponse> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.unblockUser(id));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @GetMapping("/claims")
    public ResponseEntity<Page<ClaimResponse>> getAllClaims(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) com.tpa.enums.ClaimStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return ResponseEntity.ok(claimService.searchClaims(status, null, null, null, null, null, pageable));
    }

    @PatchMapping("/claims/review")
    public ResponseEntity<ClaimResponse> reviewClaim(@Valid @RequestBody ClaimReviewRequest request, Principal principal) {
        return ResponseEntity.ok(adminService.reviewClaim(request, principal));
    }

    @PostMapping("/claims/review")
    public ResponseEntity<ClaimResponse> reviewClaimPost(@Valid @RequestBody ClaimReviewRequest request, Principal principal) {
        return ResponseEntity.ok(adminService.reviewClaim(request, principal));
    }

    @PatchMapping("/claims/{id}/approve")
    public ResponseEntity<ClaimResponse> approveClaim(@PathVariable Long id, @RequestParam(required = false) String reason, Principal principal) {
        return ResponseEntity.ok(adminService.approveClaim(id, reason != null ? reason : "Approved by admin", principal));
    }

    @PatchMapping("/claims/{id}/reject")
    public ResponseEntity<ClaimResponse> rejectClaim(@PathVariable Long id, @RequestParam String reason, Principal principal) {
        return ResponseEntity.ok(adminService.rejectClaim(id, reason, principal));
    }

    @GetMapping("/claims/{id}/ai-summary")
    public ResponseEntity<AiAnalysisResponse> getClaimAiSummary(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getClaimAiSummary(id));
    }

    @PostMapping("/claims/{id}/ai-chat")
    public ResponseEntity<AiAnalysisResponse> askAiAboutClaim(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String prompt = request.getOrDefault("prompt", "Analyze this claim");
        return ResponseEntity.ok(adminService.askAiAboutClaim(id, prompt));
    }

    @GetMapping("/monitoring")
    public ResponseEntity<Map<String, Object>> getSystemMonitoring() {
        Map<String, Object> response = new HashMap<>();

        response.put("kafka", Map.of("status", "ONLINE", "brokers", "localhost:9092", "topics", List.of("claim_events", "notifications")));

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        Page<ClaimResponse> failedClaims = claimService.searchClaims(ClaimStatus.REJECTED, null, null, null, null, null, pageable);
        response.put("failedClaims", failedClaims.getContent());

        response.put("errorLogs", List.of(
                Map.of("timestamp", LocalDateTime.now().minusHours(1), "level", "ERROR", "message", "Failed to connect to AI provider"),
                Map.of("timestamp", LocalDateTime.now().minusHours(3), "level", "WARN", "message", "Rate limit exceeded on AI provider API"),
                Map.of("timestamp", LocalDateTime.now().minusDays(1), "level", "ERROR", "message", "NullPointerException in RuleEngine")
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/carriers")
    public ResponseEntity<List<CarrierResponse>> getAllCarriers() {
        return ResponseEntity.ok(adminService.getAllCarriers());
    }

    @PatchMapping("/carriers/{id}/approve")
    public ResponseEntity<CarrierResponse> approveCarrier(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveCarrier(id));
    }

    @PatchMapping("/carriers/{id}/reject")
    public ResponseEntity<CarrierResponse> rejectCarrier(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.rejectCarrier(id));
    }

    @PatchMapping("/claims/{id}/assign-carrier")
    public ResponseEntity<ClaimResponse> assignCarrier(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        Long carrierId = body.get("carrierId");
        if (carrierId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(adminService.assignCarrierToClaim(id, carrierId));
    }
}
