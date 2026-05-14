package com.tpa.service;

import com.tpa.entity.Claim;
import com.tpa.enums.ClaimStatus;
import com.tpa.helper.AuditForensicService;
import com.tpa.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimService claimService;
    private final PaymentService paymentService;
    private final AuditForensicService auditForensicService;

    @Transactional
    public Map<String, Object> processBulkApproval(List<Long> claimIds, String approvedBy) {
        log.info("[BULK-SETTLEMENT] Processing approval for {} claims by {}", claimIds.size(), approvedBy);
        
        int successCount = 0;
        int failCount = 0;

        for (Long id : claimIds) {
            try {
                Claim claim = claimRepository.findById(id).orElseThrow();
                
                // Set status to APPROVED (or SETTLED if moving direct to payment)
                claim.setStatus(ClaimStatus.APPROVED);
                claim.setReviewedBy(approvedBy);
                claimRepository.save(claim);
                
                // Trigger Payout
                paymentService.initiateInstantPayout(claim);
                
                // Audit Trail
                auditForensicService.logAction(id, "BULK_APPROVAL", "Claim approved via Bulk Settlement Portal", approvedBy);
                
                successCount++;
            } catch (Exception e) {
                log.error("Failed to process bulk approval for claim #{}", id, e);
                failCount++;
            }
        }

        return Map.of(
            "totalProcessed", claimIds.size(),
            "success", successCount,
            "failed", failCount
        );
    }
}
