package com.tpa.service.impl;

import com.tpa.dto.request.ClaimRequest;
import com.tpa.dto.response.BulkClaimProcessResponse;
import com.tpa.dto.response.ClaimDecisionResponse;
import com.tpa.dto.response.ClaimResponse;
import com.tpa.entity.Carrier;
import com.tpa.entity.Claim;
import com.tpa.entity.ClaimAudit;
import com.tpa.entity.User;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.UserRole;
import com.tpa.exception.BadRequestException;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.helper.AuditForensicService;
import com.tpa.helper.ClaimSpecification;
import com.tpa.helper.ClaimStateMachine;
import com.tpa.helper.PdfExportService;
import com.tpa.kafka.event.ClaimNotificationEvent;
import com.tpa.kafka.producer.ClaimEventProducer;
import com.tpa.kafka.producer.ProducerService;
import com.tpa.mapper.ClaimMapper;
import com.tpa.repository.CarrierRepository;
import com.tpa.repository.ClaimAuditRepository;
import com.tpa.repository.ClaimDocumentRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.UserRepository;
import com.tpa.service.AuditLogService;
import com.tpa.service.CarrierService;
import com.tpa.service.ClaimService;
import com.tpa.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final ClaimDocumentRepository claimDocumentRepository;
    private final CarrierRepository carrierRepository;
    private final ClaimAuditRepository claimAuditRepository;

    private final ClaimMapper claimMapper;

    private final ClaimStateMachine claimStateMachine;

    private final ProducerService producerService;
    private final AuditLogService auditLogService;
    private final PdfExportService pdfExportService;
    private final CarrierService carrierService;
    private final PaymentService paymentService;
    private final AuditForensicService auditForensicService;

    private User getUser(String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isPatient(Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void validateClaimAccess(ClaimResponse claimResponse, String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isPatient(authentication)) {
            if (claimResponse.getUserEmail() == null || !claimResponse.getUserEmail().equals(username)) {
                throw new AccessDeniedException("Access denied");
            }
        }
    }

    private Pageable applyDefaultSorting(Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdDate"));
        }
        return pageable;
    }

    @Override
    @Transactional
    public ClaimResponse createClaim(ClaimRequest claimRequest, String username) {
        User user = getUser(username);
        Carrier carrier = null;

        if (claimRequest.getCarrierName() != null && !claimRequest.getCarrierName().isBlank()) {
            carrier = carrierRepository.findByCompanyNameIgnoreCase(claimRequest.getCarrierName()).orElseThrow(() -> new NoResourceFoundException("Company not found"));
        }

        Claim claim = claimMapper.toClaim(claimRequest);

        claim.setUser(user);
        claim.setCarrier(carrier);
        claim.setCarrierName(claimRequest.getCarrierName());
        claim.setClaimStatus(ClaimStatus.SUBMITTED);

        if (claimRequest.getPolicyNumber() == null || claimRequest.getPolicyNumber().isBlank()) {
            claim.setPolicyNumber("TEMP-" + System.currentTimeMillis());
        }

        claim = claimRepository.save(claim);
        auditLogService.logAction(claim.getId(), "CLAIM_CREATED", null, ClaimStatus.SUBMITTED);

        return claimMapper.toClaimResponse(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaim(Long claimId, String username) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

        ClaimResponse claimResponse = claimMapper.toClaimResponse(claim);
        validateClaimAccess(claimResponse, username);

        return claimResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimResponse> getAllClaims(Pageable pageable, String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        pageable = applyDefaultSorting(pageable);

        if (isAdmin(authentication)) {
            return claimRepository.findAll(pageable).map(claimMapper::toClaimResponse);
        }
        return searchClaims(null, null, null, null, null, username, pageable);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = "claims", key = "#claimId")
    public void processClaimDecision(Long claimId, ClaimDecisionResponse claimDecisionResponse) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));
        ClaimStatus previousStatus = claim.getClaimStatus();

        if (previousStatus == ClaimStatus.CARRIER_APPROVED || previousStatus == ClaimStatus.REJECTED || previousStatus == ClaimStatus.SETTLED) {
            return;
        }

        claimStateMachine.validateTransition(previousStatus, ClaimStatus.AI_VALIDATED);
        claim.setClaimStatus(ClaimStatus.AI_VALIDATED);

        claimRepository.save(claim);

        auditLogService.logAction(
                claimId,
                "AI_VALIDATION_PASSED",
                previousStatus,
                ClaimStatus.AI_VALIDATED
        );

        claimStateMachine.validateTransition(
                ClaimStatus.AI_VALIDATED,
                claimDecisionResponse.getClaimStatus()
        );

        claim.setClaimStatus(claimDecisionResponse.getClaimStatus());
        claim.setProcessedDate(LocalDateTime.now());

        if (claimDecisionResponse.getReasons() != null && !claimDecisionResponse.getReasons().isEmpty()) {
            claim.setRejectionReason(String.join(", ", claimDecisionResponse.getReasons()));
        }

        claimRepository.save(claim);

        auditLogService.logAction(
                claimId,
                "RULE_ENGINE_DECISION",
                ClaimStatus.AI_VALIDATED,
                claim.getClaimStatus()
        );

        ClaimNotificationEvent claimNotificationEvent = ClaimNotificationEvent.builder()
                .claimId(claim.getId())
                .policyNumber(claim.getPolicyNumber())
                .customerEmail(claim.getUser().getEmail())
                .status(claim.getClaimStatus())
                .message("Status: " + claim.getClaimStatus())
                .build();

        producerService.sendClaimNotificationEvent(claimNotificationEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimResponse> searchClaims(ClaimStatus claimStatus, LocalDateTime from, LocalDateTime to, Double minAmount, Double maxAmount, String username, Pageable pageable) {
        Specification<Claim> specification = Specification.where(ClaimSpecification.hasStatus(claimStatus))
                .and(ClaimSpecification.createdBetween(from, to))
                .and(ClaimSpecification.amountBetween(minAmount, maxAmount))
                .and(ClaimSpecification.hasUser(username));
        return claimRepository.findAll(specification, applyDefaultSorting(pageable)).map(claimMapper::toClaimResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimAudit> getClaimAudits(Long claimId, String username) {
        getClaim(claimId, username);
        return claimAuditRepository.findByClaimIdOrderByChangedAtDesc(claimId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimAudit> getClaimTimeline(Long claimId, String username) {
        return getClaimAudits(claimId, username);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportClaimReport(Long claimId, String username) {
        getClaim(claimId, username);
        return pdfExportService.exportClaimReport(claimId);
    }

    @Override
    @Transactional
    public void carrierApproveClaim(Long claimId, String username) {
        carrierService.approveClaim(claimId, username);
    }

    @Override
    @Transactional
    public void deleteClaim(Long claimId, String username) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

        User user = getUser(username);

        boolean isAdmin = user.getUserRole() == UserRole.ADMIN;

        if (!isAdmin && !claim.getUser().getEmail().equals(username)) {
            throw new AccessDeniedException("You do not have permission to delete this claim");
        }

        if (claim.getClaimStatus() != ClaimStatus.SUBMITTED && claim.getClaimStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new BadRequestException("Cannot delete claim in " + claim.getClaimStatus() + " status");
        }
        claimAuditRepository.deleteByClaimId(claimId);
        claimDocumentRepository.deleteByClaimId(claimId);
        claimRepository.delete(claim);
    }

    @Override
    @Transactional
    public BulkClaimProcessResponse processBulkApproval(List<Long> claimIds, String approvedBy) {
        log.info("[BULK-SETTLEMENT] Processing approval for {} claims by {}", claimIds.size(), approvedBy);

        int successCount = 0;
        int failCount = 0;

        for (Long claimId : claimIds) {
            try {
                Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

                claim.setClaimStatus(ClaimStatus.APPROVED);
                claim.setReviewedBy(approvedBy);
                claim.setReviewedAt(LocalDateTime.now());

                claimRepository.save(claim);

                paymentService.initiateInstantPayout(claim);
                auditForensicService.logAction(claimId, "BULK_APPROVAL", "Claim approved via Bulk Settlement Portal", approvedBy);

                successCount++;
            } catch (Exception e) {
                log.error("Failed to process bulk approval for claim #{}", claimId, e);

                failCount++;
            }
        }

        return BulkClaimProcessResponse.builder()
                .totalProcessed(claimIds.size())
                .success(successCount)
                .failed(failCount)
                .build();
    }
}