package com.tpa.service.impl;

import com.tpa.dto.response.claim.CarrierClaimDetailResponse;
import com.tpa.dto.response.claim.PolicyStatusResponse;
import com.tpa.entity.Carrier;
import com.tpa.entity.Claim;
import com.tpa.enums.ClaimStatus;
import com.tpa.exception.BadRequestException;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.helper.ClaimStateMachine;
import com.tpa.helper.PolicyValidationHelper;
import com.tpa.mapper.CarrierClaimMapper;
import com.tpa.service.AuditLogService;
import com.tpa.service.NotificationService;
import com.tpa.kafka.event.ClaimNotificationEvent;
import com.tpa.kafka.producer.ProducerService;
import com.tpa.repository.CarrierRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.service.CarrierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository carrierRepository;
    private final ClaimRepository claimRepository;

    private final ProducerService producerService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final ClaimStateMachine claimStateMachine;
    private final PolicyValidationHelper policyValidationHelper;

    private final CarrierClaimMapper carrierClaimMapper;

    private Carrier getCarrierByUsername(String email) {
        return carrierRepository.findByUser_Email(email).orElseThrow(() -> new NoResourceFoundException("Carrier profile not found. Ensure you are logged in as a Carrier."));
    }

    private Claim getClaimForCarrier(Long claimId, Carrier carrier) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("Claim not found: " + claimId));

        if (claim.getCarrier() == null || !claim.getCarrier().getId().equals(carrier.getId())) {
            throw new BadRequestException("Claim #" + claimId + " is not assigned to your carrier account.");
        }
        return claim;
    }

    private void guardFinalState(Claim claim) {
        if (claim.getClaimStatus() == ClaimStatus.CARRIER_APPROVED || claim.getClaimStatus() == ClaimStatus.REJECTED || claim.getClaimStatus() == ClaimStatus.SETTLED) {
            throw new BadRequestException("Claim #" + claim.getId() + " is already " + claim.getClaimStatus() + " and cannot be modified.");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<CarrierClaimDetailResponse> getAssignedClaims(String username) {
        Carrier carrier = getCarrierByUsername(username);
        List<Claim> claims = claimRepository.findByCarrier_Id(carrier.getId());

        log.info("Carrier {} fetched {} assigned claims", carrier.getCompanyName(), claims.size());
        return carrierClaimMapper.toCarrierClaimDetailResponses(claims);
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierClaimDetailResponse getClaimDetail(Long claimId, String username) {
        Carrier carrier = getCarrierByUsername(username);
        Claim claim = getClaimForCarrier(claimId, carrier);

        return carrierClaimMapper.toCarrierClaimDetailResponse(claim);
    }

    @Override
    @Transactional
    public void validatePolicy(Long claimId, String username) {
        Carrier carrier = getCarrierByUsername(username);
        Claim claim = getClaimForCarrier(claimId, carrier);

        String note = "[" + LocalDateTime.now() + "] Policy validated by " + carrier.getCompanyName();
        claim.setReviewNotes(claim.getReviewNotes() != null ? claim.getReviewNotes() + "\n" + note : note);

        claimRepository.save(claim);
        log.info("Claim {} policy validated by carrier {}", claimId, carrier.getCompanyName());

        notificationService.notifyAllAdmins(
                "\uD83D\uDCCB Claim #" + claimId + " Validated by Carrier",
                "Claim #" + claimId + " (Policy: " + claim.getPolicyNumber() + ") has been validated by carrier " + carrier.getCompanyName() + ".",
                "/claims/" + claimId);
    }

    @Override
    @Transactional
    public void approveClaim(Long claimId, String username) {
        Carrier carrier = getCarrierByUsername(username);
        Claim claim = getClaimForCarrier(claimId, carrier);
        ClaimStatus previousStatus = claim.getClaimStatus();

        log.info("Attempting transition: {} → {}", previousStatus, ClaimStatus.CARRIER_APPROVED);

        if (previousStatus != ClaimStatus.SUBMITTED && previousStatus != ClaimStatus.AI_VALIDATED && previousStatus != ClaimStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Carrier can only approve claims before Admin approval");
        }

        claimStateMachine.validateTransition(previousStatus, ClaimStatus.CARRIER_APPROVED);

        claim.setClaimStatus(ClaimStatus.CARRIER_APPROVED);
        claim.setProcessedDate(LocalDateTime.now());
        claim.setReviewedBy(carrier.getCompanyName());
        claim.setReviewedAt(LocalDateTime.now());
        claimRepository.save(claim);

        auditLogService.logAction(claimId, "CARRIER_APPROVAL", previousStatus, ClaimStatus.CARRIER_APPROVED);
        log.info("Claim {} CARRIER_APPROVED by carrier {}", claimId, carrier.getCompanyName());

        producerService.sendClaimNotificationEvent(ClaimNotificationEvent.builder()
                .claimId(claim.getId()).policyNumber(claim.getPolicyNumber())
                .customerEmail(claim.getUser().getEmail()).status(ClaimStatus.CARRIER_APPROVED)
                .message("Your claim has been APPROVED by the carrier and is awaiting final admin approval.").build());
        notificationService.notifyAllAdmins(
                "\uD83D\uDCCB Claim #" + claimId + " Approved by Carrier",
                "Claim #" + claimId + " (Policy: " + claim.getPolicyNumber() + ") has been approved by carrier " + carrier.getCompanyName() + ". It is now awaiting your final approval to release payment.",
                "/claims/" + claimId
        );
    }

    @Override
    @Transactional
    public void rejectClaim(Long claimId, String username) {
        Carrier carrier = getCarrierByUsername(username);
        Claim claim = getClaimForCarrier(claimId, carrier);
        guardFinalState(claim);

        claim.setClaimStatus(ClaimStatus.REJECTED);
        claim.setProcessedDate(LocalDateTime.now());
        claim.setReviewedBy(carrier.getCompanyName());
        claim.setReviewedAt(LocalDateTime.now());
        claim.setRejectionReason("Rejected by carrier: " + carrier.getCompanyName());

        claimRepository.save(claim);
        log.info("Claim {} REJECTED by carrier {}", claimId, carrier.getCompanyName());

        producerService.sendClaimNotificationEvent(ClaimNotificationEvent.builder()
                .claimId(claim.getId()).policyNumber(claim.getPolicyNumber())
                .customerEmail(claim.getUser().getEmail()).status(ClaimStatus.REJECTED)
                .message("Your claim has been REJECTED by the carrier.").build());

        notificationService.notifyAllAdmins(
                "Claim #" + claimId + " Rejected by Carrier",
                "Claim #" + claimId + " (Policy: " + claim.getPolicyNumber() + ") has been rejected by carrier " + carrier.getCompanyName() + ".",
                "/claims/" + claimId);
    }

    @Override
    @Transactional
    public void addRemark(Long claimId, String remark, String username) {
        Carrier carrier = getCarrierByUsername(username);

        Claim claim = getClaimForCarrier(claimId, carrier);
        String entry = "[" + LocalDateTime.now() + "] " + carrier.getCompanyName() + ": " + remark;
        claim.setReviewNotes(claim.getReviewNotes() != null ? claim.getReviewNotes() + "\n" + entry : entry);

        claimRepository.save(claim);
        log.info("Remark added to claim {} by carrier {}", claimId, carrier.getCompanyName());
    }

    @Override
    @Transactional
    public void flagSuspicious(Long claimId, String username) {
        Carrier carrier = getCarrierByUsername(username);
        Claim claim = getClaimForCarrier(claimId, carrier);

        String entry = "SUSPICIOUS — flagged by " + carrier.getCompanyName() + " at " + LocalDateTime.now();

        claim.setRiskFlags(claim.getRiskFlags() != null ? claim.getRiskFlags() + " | " + entry : entry);
        claim.setRiskScore(claim.getRiskScore() != null ? Math.min(100.0, claim.getRiskScore() + 25.0) : 75.0);

        claimRepository.save(claim);
        log.info("Claim {} flagged suspicious by carrier {}", claimId, carrier.getCompanyName());
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyStatusResponse getPolicyStatus(Long claimId, String username) {
        Carrier carrier = getCarrierByUsername(username);
        Claim claim = getClaimForCarrier(claimId, carrier);

        return policyValidationHelper.buildPolicyStatus(claim);
    }

}
