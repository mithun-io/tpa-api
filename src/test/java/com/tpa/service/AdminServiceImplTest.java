package com.tpa.service;

import com.tpa.dto.request.claim.ClaimReviewRequest;
import com.tpa.dto.response.claim.ClaimResponse;
import com.tpa.entity.*;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import com.tpa.exception.ConflictException;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.helper.ClaimStateMachine;
import com.tpa.kafka.producer.ClaimEventProducer;
import com.tpa.kafka.producer.ProducerService;
import com.tpa.mapper.CarrierMapper;
import com.tpa.mapper.ClaimMapper;
import com.tpa.mapper.UserMapper;
import com.tpa.repository.CarrierRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.UserRepository;
import com.tpa.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TC-061 to TC-068: AdminService Unit Tests
 * Tests admin claim review, approve/reject, carrier management,
 * user blocking/unblocking, status transition validation,
 * and carrier assignment guard conditions.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService - Admin Workflow Unit Tests")
class AdminServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private ClaimMapper claimMapper;
    @Mock private CarrierMapper carrierMapper;
    @Mock private ProducerService producerService;
    @Mock private ClaimRepository claimRepository;
    @Mock private AiClaimAssistantService aiClaimAssistantService;
    @Mock private AuditLogService auditLogService;
    @Mock private ClaimStateMachine claimStateMachine;
    @Mock private NotificationService notificationService;
    @Mock private CarrierRepository carrierRepository;
    @Mock private ClaimEventProducer claimEventProducer;
    @Mock private com.tpa.helper.EmailService emailService;
    @Mock private RefreshTokenService refreshTokenService;

    @InjectMocks
    private com.tpa.service.impl.AdminServiceImpl adminService;

    private User patientUser;
    private User adminUser;
    private Claim submittedClaim;
    private Carrier carrier;
    private Principal principal;

    @BeforeEach
    void setUp() {
        patientUser = TestDataFactory.buildPatientUser();
        patientUser.setId(1L);

        adminUser = TestDataFactory.buildAdminUser();
        adminUser.setId(99L);

        User carrierUser = TestDataFactory.buildCarrierUser();
        carrierUser.setId(10L);
        carrier = TestDataFactory.buildCarrier(carrierUser);
        carrier.setId(5L);

        submittedClaim = TestDataFactory.buildSubmittedClaim(patientUser);
        submittedClaim.setId(100L);
        submittedClaim.setCarrier(carrier);

        principal = () -> "admin@test.com";
    }

    // ── TC-061 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-061: reviewClaim validates state machine transition and saves updated claim")
    void reviewClaim_shouldValidateTransitionAndPersist() {
        when(claimRepository.findById(100L)).thenReturn(Optional.of(submittedClaim));
        doNothing().when(claimStateMachine).validateTransition(any(), any());
        when(claimRepository.save(any())).thenReturn(submittedClaim);
        when(claimMapper.toClaimResponse(any())).thenReturn(new ClaimResponse());

        ClaimReviewRequest request = TestDataFactory.buildReviewRequest(100L, ClaimStatus.UNDER_REVIEW, "Needs more docs");
        adminService.reviewClaim(request, principal);

        verify(claimStateMachine).validateTransition(ClaimStatus.SUBMITTED, ClaimStatus.UNDER_REVIEW);
        verify(claimRepository).save(any());
        verify(auditLogService).logAction(eq(100L), eq("ADMIN_REVIEW"), any(), any());
    }

    // ── TC-062 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-062: approveClaim sets ADMIN_APPROVED status and records audit")
    void approveClaim_shouldSetAdminApprovedAndAudit() {
        when(claimRepository.findById(100L)).thenReturn(Optional.of(submittedClaim));
        doNothing().when(claimStateMachine).validateTransition(any(), any());
        when(claimRepository.save(any())).thenReturn(submittedClaim);
        when(claimMapper.toClaimResponse(any())).thenReturn(new ClaimResponse());

        adminService.approveClaim(100L, "Approved after review", principal);

        verify(claimRepository).save(argThat(c -> c.getClaimStatus() == ClaimStatus.ADMIN_APPROVED));
        verify(auditLogService).logAction(eq(100L), eq("ADMIN_APPROVED"), any(), eq(ClaimStatus.ADMIN_APPROVED));
        verify(producerService).sendClaimNotificationEvent(any());
    }

    // ── TC-063 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-063: rejectClaim sets REJECTED status, persists rejection reason and fires notification")
    void rejectClaim_shouldSetRejectedStatusWithReason() {
        when(claimRepository.findById(100L)).thenReturn(Optional.of(submittedClaim));
        doNothing().when(claimStateMachine).validateTransition(any(), any());
        when(claimRepository.save(any())).thenReturn(submittedClaim);
        when(claimMapper.toClaimResponse(any())).thenReturn(new ClaimResponse());

        adminService.rejectClaim(100L, "Fraudulent claim detected", principal);

        verify(claimRepository).save(argThat(c ->
                c.getClaimStatus() == ClaimStatus.REJECTED &&
                        "Fraudulent claim detected".equals(c.getRejectionReason())
        ));
        verify(notificationService).createNotification(any(), any(), any(), any());
    }

    // ── TC-064 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-064: blockUser throws IllegalArgumentException when blocking an ADMIN user")
    void blockUser_adminUser_shouldThrowIllegalArgument() {
        when(userRepository.findById(99L)).thenReturn(Optional.of(adminUser));

        assertThatThrownBy(() -> adminService.blockUser(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("admin cannot be blocked");
    }

    // ── TC-065 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-065: blockUser successfully sets BLOCKED status for active patient")
    void blockUser_activePatient_shouldSetBlockedStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(patientUser));
        when(userMapper.toUserResponse(any())).thenReturn(new com.tpa.dto.response.user.UserResponse());

        adminService.blockUser(1L);

        assertThat(patientUser.getUserStatus()).isEqualTo(UserStatus.BLOCKED);
    }

    // ── TC-066 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-066: approveCarrier sets carrier user status to ACTIVE")
    void approveCarrier_shouldActivateCarrierUser() {
        carrier.getUser().setUserStatus(UserStatus.INACTIVE);
        when(carrierRepository.findById(5L)).thenReturn(Optional.of(carrier));
        when(userRepository.saveAndFlush(any())).thenReturn(carrier.getUser());

        // TransactionSynchronizationManager.registerSynchronization() requires an active transaction.
        // In a pure Mockito unit test there is none, so we expect that exception but verify the
        // core side-effect (status flip + saveAndFlush) still happened before it was thrown.
        try {
            adminService.approveCarrier(5L);
        } catch (IllegalStateException e) {
            // expected in unit-test context — no active Spring transaction
        }

        assertThat(carrier.getUser().getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).saveAndFlush(carrier.getUser());
    }

    // ── TC-067 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-067: assignClaimToCarrier throws IllegalArgumentException when carrier is not ACTIVE")
    void assignClaimToCarrier_inactiveCarrier_shouldThrowIllegalArgument() {
        carrier.getUser().setUserStatus(UserStatus.INACTIVE);
        when(claimRepository.findById(100L)).thenReturn(Optional.of(submittedClaim));
        when(carrierRepository.findById(5L)).thenReturn(Optional.of(carrier));

        assertThatThrownBy(() -> adminService.assignClaimToCarrier(100L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not active");
    }

    // ── TC-068 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-068: assignClaimToCarrier throws NoResourceFoundException when claim does not exist")
    void assignClaimToCarrier_nonExistentClaim_shouldThrowNotFound() {
        when(claimRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.assignClaimToCarrier(999L, 5L))
                .isInstanceOf(NoResourceFoundException.class);
    }
}
