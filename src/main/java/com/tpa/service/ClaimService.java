package com.tpa.service;

import com.tpa.dto.request.claim.ClaimQueryRequest;
import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.claim.*;
import com.tpa.entity.ClaimAudit;
import com.tpa.entity.ClaimQuery;
import com.tpa.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ClaimService {

    ClaimResponse createClaim(ClaimRequest claimRequest, String username);

    ClaimResponse getClaim(Long claimId, String username);

    ClaimResponse getClaim(Long claimId);

    Page<ClaimResponse> getAllClaims(Pageable pageable, String username);

    void processClaimDecision(Long claimId, ClaimDecisionResponse claimDecisionResponse);

    Page<ClaimResponse> searchClaims(ClaimStatus claimStatus, LocalDateTime from, LocalDateTime to, Double minAmount, Double maxAmount, String username, Pageable pageable);

    List<ClaimAudit> getClaimAudits(Long claimId, String username);

    byte[] exportClaimReport(Long claimId, String username);

    void carrierApproveClaim(Long claimId, String username);

    void deleteClaim(Long claimId, String username);

    BulkClaimProcessResponse processBulkApproval(List<Long> claimIds, String approvedBy);

    List<ClaimQueryResponse> getClaimQueries(Long claimId, String username);

    ClaimQueryResponse createClaimQuery(Long claimId, ClaimQueryRequest claimQueryRequest, String username);


    void broadcastStatusUpdate(Long claimId, String status, String message);

    void sendUserNotification(String userEmail, String title, String message);
}