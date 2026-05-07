package com.tpa.controller;

import com.tpa.service.BulkClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/claims/bulk")
@RequiredArgsConstructor
public class BulkClaimController {

    private final BulkClaimService bulkClaimService;

    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> bulkApprove(
            @RequestBody List<Long> claimIds,
            @RequestParam String approvedBy) {
        return ResponseEntity.ok(bulkClaimService.processBulkApproval(claimIds, approvedBy));
    }
}
