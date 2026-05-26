package com.tpa.service.impl;

import com.tpa.dto.response.analytics.AiAnalysisResponse;
import com.tpa.dto.response.analytics.MonitoringResponse;
import com.tpa.dto.response.claim.ClaimResponse;
import com.tpa.dto.response.user.CarrierResponse;
import com.tpa.dto.response.user.UserResponse;
import com.tpa.entity.Carrier;
import com.tpa.service.NotificationService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import com.tpa.entity.User;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import com.tpa.exception.ConflictException;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.kafka.producer.ProducerService;
import com.tpa.mapper.UserMapper;
import com.tpa.repository.UserRepository;
import com.tpa.service.AdminService;
import com.tpa.dto.request.claim.ClaimReviewRequest;
import com.tpa.entity.Claim;
import com.tpa.enums.ClaimStatus;
import com.tpa.kafka.event.ClaimNotificationEvent;
import com.tpa.mapper.ClaimMapper;
import com.tpa.mapper.CarrierMapper;
import com.tpa.repository.CarrierRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.kafka.producer.ClaimEventProducer;
import com.tpa.service.AiClaimAssistantService;
import com.tpa.service.AuditLogService;
import com.tpa.helper.ClaimStateMachine;
import com.tpa.helper.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final ClaimMapper claimMapper;
    private final CarrierMapper carrierMapper;

    private final CarrierRepository carrierRepository;
    private final ClaimRepository claimRepository;
    
    private final AiClaimAssistantService aiClaimAssistantService;
    private final AuditLogService auditLogService;
    
    private final ProducerService producerService;
    private final ClaimStateMachine claimStateMachine;
    private final NotificationService notificationService;
    private final EmailService emailService;

    private final ClaimEventProducer claimEventProducer;
    

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NoResourceFoundException("user not found"));
    }

    private void validateUserTransition(UserStatus currentStatus, UserStatus target) {
        if (currentStatus == target) {
            throw new ConflictException("user already in " + currentStatus + " state");
        }

        boolean isValid = switch (currentStatus) {
            case ACTIVE -> target == UserStatus.INACTIVE || target == UserStatus.BLOCKED;
            case INACTIVE, PENDING, BLOCKED -> target == UserStatus.ACTIVE;
            default -> throw new IllegalStateException("unexpected status: " + currentStatus);
        };

        if (!isValid) {
            throw new IllegalArgumentException("invalid status transition");
        }
    }

    @Transactional
    @Override
    public UserResponse blockUser(Long id) {
        User user = getUser(id);
        if (user.getUserRole() == UserRole.ADMIN) {
            throw new IllegalArgumentException("admin cannot be blocked!");
        }

        validateUserTransition(user.getUserStatus(), UserStatus.BLOCKED);
        user.setUserStatus(UserStatus.BLOCKED);

        return userMapper.toUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponse unblockUser(Long id) {
        User user = getUser(id);
        validateUserTransition(user.getUserStatus(), UserStatus.ACTIVE);
        user.setUserStatus(UserStatus.ACTIVE);

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users;

        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search.trim(), search.trim(), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(userMapper::toUserResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CarrierResponse> getAllCarriers(String companyName, UserStatus userStatus, int page, int size, String sortBy, boolean desc) {
        String normalizedCompanyName = companyName == null || companyName.isBlank() ? null : companyName.trim();
        String normalizedSortBy = normalizeCarrierSortBy(sortBy);
        Sort sort = desc ? Sort.by(normalizedSortBy).descending() : Sort.by(normalizedSortBy).ascending();
        Pageable pageable = PageRequest.of(Math.max(page, 1) - 1, size, sort);
        Page<Carrier> carriers;

        if (normalizedCompanyName == null && userStatus == null) {
            carriers = carrierRepository.findAll(pageable);
        } else if (normalizedCompanyName != null && userStatus != null) {
            carriers = carrierRepository.findByCompanyNameContainingIgnoreCaseAndUser_UserStatusIn(normalizedCompanyName, carrierApprovalStatuses(userStatus), pageable);
        } else if (normalizedCompanyName != null) {
            carriers = carrierRepository.findByCompanyNameContainingIgnoreCase(normalizedCompanyName, pageable);
        } else {
            carriers = carrierRepository.findByUser_UserStatusIn(carrierApprovalStatuses(userStatus), pageable);
        }

        return carriers.map(carrierMapper::toCarrierResponse);
    }

    private String normalizeCarrierSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank() || "createdAt".equals(sortBy)) {
            return "user.createdAt";
        }
        return sortBy;
    }

    private List<UserStatus> carrierApprovalStatuses(UserStatus userStatus) {
        if (userStatus == UserStatus.INACTIVE || userStatus == UserStatus.PENDING) {
            return List.of(UserStatus.PENDING, UserStatus.INACTIVE);
        }
        return List.of(userStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserResponse> getAllPatients(String username, String email, UserStatus userStatus, int page, int size, String sortBy, boolean desc) {
        Sort sort = desc ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<User> patients;

        if (username == null && email == null && userStatus == null) {
            patients = userRepository.findByUserRole(UserRole.PATIENT, pageable);
        } else if (username != null && email != null && userStatus != null) {
            patients = userRepository.findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusAndUserRole(username, email, userStatus, UserRole.PATIENT, pageable);
        } else if (username != null) {
            patients = userRepository.findByUsernameContainingIgnoreCaseAndUserRole(username, UserRole.PATIENT, pageable);
        } else if (email != null) {
            patients = userRepository.findByEmailContainingIgnoreCaseAndUserRole(email, UserRole.PATIENT, pageable);
        } else {
            patients = userRepository.findByUserStatusAndUserRole(userStatus, UserRole.PATIENT, pageable);
        }

        if (patients.isEmpty()) {
            throw new NoResourceFoundException("patients not found");
        }

        return patients.map(userMapper::toUserResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ClaimResponse> getAllClaims(ClaimStatus claimStatus, LocalDateTime createdAt, int page, int size, String sortBy, boolean desc) {
        Sort sort = desc ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Claim> claims;

        if (claimStatus == null && createdAt == null) {
            claims = claimRepository.findAll(pageable);
        } else if (claimStatus != null && createdAt != null) {
            claims = claimRepository.findByClaimStatusAndCreatedDateAfter(claimStatus, createdAt, pageable);
        } else if (claimStatus != null) {
            claims = claimRepository.findByClaimStatus(claimStatus, pageable);
        } else {
            claims = claimRepository.findByCreatedDateAfter(createdAt, pageable);
        }

        if (claims.isEmpty()) {
            throw new NoResourceFoundException("claims not found");
        }

        return claims.map(claimMapper::toClaimResponse);
    }

    @Transactional
    @Override
    @Caching(evict = {@CacheEvict(value = "claims", key = "#request.claimId"), @CacheEvict(value = "aiSummaries", key = "#request.claimId")})
    public ClaimResponse reviewClaim(ClaimReviewRequest claimReviewRequest, Principal principal) {
        Claim claim = claimRepository.findById(claimReviewRequest.getClaimId()).orElseThrow(() -> new NoResourceFoundException("claim not found"));

        claimStateMachine.validateTransition(claim.getClaimStatus(), claimReviewRequest.getClaimStatus());

        ClaimStatus previousStatus = claim.getClaimStatus();
        claim.setClaimStatus(claimReviewRequest.getClaimStatus());
        claim.setRejectionReason(claimReviewRequest.getReviewNotes());
        claim.setProcessedDate(LocalDateTime.now());
        claim.setReviewedBy(principal.getName());
        claim.setReviewedAt(LocalDateTime.now());
        claim.setReviewNotes(claimReviewRequest.getReviewNotes());

        claimRepository.save(claim);
        log.info("Admin {} reviewed claim {} with status {}", principal.getName(), claim.getId(), claim.getClaimStatus());

        auditLogService.logAction(claim.getId(), "ADMIN_REVIEW", previousStatus, claim.getClaimStatus());

        ClaimNotificationEvent claimNotificationEvent = ClaimNotificationEvent.builder()
                .claimId(claim.getId())
                .policyNumber(claim.getPolicyNumber())
                .customerEmail(claim.getUser().getEmail())
                .status(claim.getClaimStatus())
                .message("Your claim has been " + claim.getClaimStatus() + ". Notes: " + claimReviewRequest.getReviewNotes())
                .build();

        producerService.sendClaimNotificationEvent(claimNotificationEvent);
        notificationService.createNotification(claim.getUser(), "Claim " + claim.getClaimStatus(), claimNotificationEvent.getMessage(), "/claims/" + claim.getId());

        return claimMapper.toClaimResponse(claim);
    }

    @Transactional
    @Override
    @Caching(evict = {@CacheEvict(value = "claims", key = "#claimId"), @CacheEvict(value = "aiSummaries", key = "#claimId")})
    public ClaimResponse approveClaim(Long claimId, String reason, Principal principal) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("claim not found"));

        claimStateMachine.validateTransition(claim.getClaimStatus(), ClaimStatus.ADMIN_APPROVED);

        ClaimStatus previousStatus = claim.getClaimStatus();
        claim.setClaimStatus(ClaimStatus.ADMIN_APPROVED);
        claim.setProcessedDate(LocalDateTime.now());
        claim.setReviewedBy(principal.getName());
        claim.setReviewedAt(LocalDateTime.now());
        claim.setReviewNotes(reason);

        claimRepository.save(claim);
        log.info("Admin {} APPROVED claim {}", principal.getName(), claim.getId());

        auditLogService.logAction(claim.getId(), "ADMIN_APPROVED", previousStatus, ClaimStatus.ADMIN_APPROVED);

        ClaimNotificationEvent claimNotificationEvent = ClaimNotificationEvent.builder()
                .claimId(claim.getId())
                .policyNumber(claim.getPolicyNumber())
                .customerEmail(claim.getUser().getEmail())
                .status(ClaimStatus.ADMIN_APPROVED)
                .message("Your claim has been APPROVED. Notes: " + reason)
                .build();
        producerService.sendClaimNotificationEvent(claimNotificationEvent);
        notificationService.createNotification(claim.getUser(), "Claim Approved", claimNotificationEvent.getMessage(), "/claims/" + claim.getId());

        return claimMapper.toClaimResponse(claim);
    }

    @Transactional
    @Override
    @Caching(evict = {@CacheEvict(value = "claims", key = "#claimId"), @CacheEvict(value = "aiSummaries", key = "#claimId")})
    public ClaimResponse rejectClaim(Long claimId, String reason, Principal principal) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("claim not found"));

        claimStateMachine.validateTransition(claim.getClaimStatus(), ClaimStatus.REJECTED);

        ClaimStatus previousStatus = claim.getClaimStatus();
        claim.setClaimStatus(ClaimStatus.REJECTED);
        claim.setRejectionReason(reason);
        claim.setProcessedDate(LocalDateTime.now());
        claim.setReviewedBy(principal.getName());
        claim.setReviewedAt(LocalDateTime.now());
        claim.setReviewNotes(reason);

        claimRepository.save(claim);
        log.info("Admin {} REJECTED claim {}", principal.getName(), claim.getId());

        auditLogService.logAction(claim.getId(), "ADMIN_REJECTED", previousStatus, ClaimStatus.REJECTED);

        ClaimNotificationEvent claimNotificationEvent = ClaimNotificationEvent.builder()
                .claimId(claim.getId())
                .policyNumber(claim.getPolicyNumber())
                .customerEmail(claim.getUser().getEmail())
                .status(ClaimStatus.REJECTED)
                .message("Your claim has been REJECTED. Reason: " + reason)
                .build();
        producerService.sendClaimNotificationEvent(claimNotificationEvent);
        notificationService.createNotification(claim.getUser(), "Claim Rejected", claimNotificationEvent.getMessage(), "/claims/" + claim.getId());

        return claimMapper.toClaimResponse(claim);
    }

    @Override
    @Transactional
    public CarrierResponse approveCarrier(Long carrierId) {
        Carrier carrier = carrierRepository.findById(carrierId).orElseThrow(() -> new NoResourceFoundException("carrier not found"));

        User carrierUser = carrier.getUser();
        carrierUser.setUserStatus(UserStatus.ACTIVE);
        userRepository.saveAndFlush(carrierUser);
        log.info("Carrier {} APPROVED by admin", carrier.getCompanyName());

        final Long cId = carrier.getId();
        final String cName = carrier.getCompanyName();
        final String cEmail = carrierUser.getEmail();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    emailService.sendCarrierApprovalEmail(cEmail, cName);
                } catch (Exception e) {
                    log.warn("Failed to send carrier approval email to {}: {}", cEmail, e.getMessage());
                }
                try {
                    claimEventProducer.publishCarrierApprovedEvent(cId, cName, cEmail);
                } catch (Exception e) {
                    log.warn("Failed to publish carrier-approved Kafka event for {}: {}", cId, e.getMessage());
                }
            }
        });

        return carrierMapper.toCarrierResponse(carrier);
    }

    @Override
    @Transactional
    public CarrierResponse rejectCarrier(Long carrierId) {
        Carrier carrier = carrierRepository.findById(carrierId).orElseThrow(() -> new NoResourceFoundException("carrier not found"));

        User carrierUser = carrier.getUser();
        carrierUser.setUserStatus(UserStatus.BLOCKED);
        userRepository.saveAndFlush(carrierUser);
        log.info("Carrier {} REJECTED by admin", carrier.getCompanyName());

        final String cEmail = carrierUser.getEmail();
        final String cName = carrier.getCompanyName();

        TransactionSynchronizationManager.registerSynchronization(new org.springframework.transaction.support.TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    emailService.sendCarrierRejectionEmail(cEmail, cName);
                } catch (Exception e) {
                    log.warn("Failed to send carrier rejection email to {}: {}", cEmail, e.getMessage());
                }
            }
        });

        return carrierMapper.toCarrierResponse(carrier);
    }

    @Override
    @Transactional
    public ClaimResponse assignClaimToCarrier(Long claimId, Long carrierId) {
        if (carrierId == null) {
            throw new IllegalArgumentException("carrier id is required");
        }

        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new NoResourceFoundException("Claim not found"));
        Carrier carrier = carrierRepository.findById(carrierId).orElseThrow(() -> new NoResourceFoundException("Carrier not found"));

        if (carrier.getUser().getUserStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Carrier is not active and cannot be assigned to claims");
        }

        claim.setCarrier(carrier);
        claimRepository.save(claim);

        log.info("Claim {} assigned to carrier {} by admin", claimId, carrier.getCompanyName());
        return claimMapper.toClaimResponse(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public AiAnalysisResponse getClaimAiSummary(Long claimId) {
        if (!claimRepository.existsById(claimId)) {
            throw new NoResourceFoundException("Claim not found");
        }

        log.info("Requesting AI summary for claim {}", claimId);
        return aiClaimAssistantService.analyzeClaim(claimId, "Please summarize this claim for an admin reviewer. Highlight any discrepancies or high-risk factors.");
    }

    @Override
    @Transactional(readOnly = true)
    public AiAnalysisResponse askAiAboutClaim(Long claimId, String prompt) {
        if (!claimRepository.existsById(claimId)) {
            throw new NoResourceFoundException("Claim not found");
        }

        log.info("Requesting custom AI analysis for claim {} with prompt: {}", claimId, prompt);
        return aiClaimAssistantService.analyzeClaim(claimId, prompt);
    }

    @Override
    @Transactional(readOnly = true)
    public MonitoringResponse getSystemMonitoring() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        Page<Claim> failedClaims = claimRepository.findByClaimStatus(ClaimStatus.REJECTED, pageable);

        Map<String, Object> kafka = Map.of(
                "status", "ONLINE",
                "brokers", "localhost:9092",
                "topics", List.of("claim_events", "notifications")
        );

        List<Map<String, Object>> errorLogs = List.of(
                Map.of(
                        "timestamp", LocalDateTime.now().minusHours(1),
                        "level", "ERROR",
                        "message", "Failed to connect to AI provider"
                ),
                Map.of(
                        "timestamp", LocalDateTime.now().minusHours(3),
                        "level", "WARN",
                        "message", "Rate limit exceeded on AI provider API"
                ),
                Map.of(
                        "timestamp", LocalDateTime.now().minusDays(1),
                        "level", "ERROR",
                        "message", "NullPointerException in RuleEngine"
                ));

        return new MonitoringResponse(kafka, claimMapper.toClaimResponses(failedClaims.getContent()), errorLogs);
    }
}
