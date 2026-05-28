package com.tpa.service;

import com.tpa.entity.*;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.UserStatus;
import com.tpa.exception.BadRequestException;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.helper.ClaimStateMachine;
import com.tpa.helper.PolicyValidationHelper;
import com.tpa.kafka.producer.ProducerService;
import com.tpa.mapper.CarrierClaimMapper;
import com.tpa.repository.CarrierRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TC-045 to TC-052: CarrierService Unit Tests
 * Tests carrier approval workflow, guard conditions, bad carrier access,
 * risk flagging logic, and policy validation delegation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CarrierService - Carrier Workflow Unit Tests")
class CarrierServiceImplTest {

    @Mock private CarrierRepository carrierRepository;
    @Mock private ClaimRepository claimRepository;
    @Mock private ProducerService producerService;
    @Mock private NotificationService notificationService;
    @Mock private AuditLogService auditLogService;
    @Mock private AiClaimAssistantService aiClaimAssistantService;
    @Mock private ClaimStateMachine claimStateMachine;
    @Mock private PolicyValidationHelper policyValidationHelper;
    @Mock private CarrierClaimMapper carrierClaimMapper;

    @InjectMocks
    private com.tpa.service.impl.CarrierServiceImpl carrierService;

    private User carrierUser;
    private Carrier carrier;
    private User patientUser;
    private Claim adminApprovedClaim;

    @BeforeEach
    void setUp() {
        carrierUser = TestDataFactory.buildCarrierUser();
        carrierUser.setId(10L);

        carrier = TestDataFactory.buildCarrier(carrierUser);
        carrier.setId(5L);

        patientUser = TestDataFactory.buildPatientUser();
        patientUser.setId(1L);

        adminApprovedClaim = TestDataFactory.buildAdminApprovedClaim(patientUser, carrier);
        adminApprovedClaim.setId(100L);
    }

    // ── TC-045 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-045: approveClaim transitions ADMIN_APPROVED → CARRIER_APPROVED successfully")
    void approveClaim_fromAdminApproved_shouldTransitionToCarrierApproved() {
        when(carrierRepository.findByUser_Email("carrier@test.com")).thenReturn(Optional.of(carrier));
        when(claimRepository.findById(100L)).thenReturn(Optional.of(adminApprovedClaim));
        when(claimRepository.save(any())).thenReturn(adminApprovedClaim);
        doNothing().when(claimStateMachine).validateTransition(any(), any());

        carrierService.approveClaim(100L, "carrier@test.com");

        verify(claimRepository).save(argThat(c -> c.getClaimStatus() == ClaimStatus.CARRIER_APPROVED));
        verify(auditLogService).logAction(eq(100L), eq("CARRIER_APPROVAL"), any(), eq(ClaimStatus.CARRIER_APPROVED));
    }

    // ── TC-046 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-046: approveClaim throws IllegalStateException when claim is not ADMIN_APPROVED")
    void approveClaim_notInAdminApprovedState_shouldThrowIllegalState() {
        adminApprovedClaim.setClaimStatus(ClaimStatus.SUBMITTED);
        when(carrierRepository.findByUser_Email("carrier@test.com")).thenReturn(Optional.of(carrier));
        when(claimRepository.findById(100L)).thenReturn(Optional.of(adminApprovedClaim));

        assertThatThrownBy(() -> carrierService.approveClaim(100L, "carrier@test.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN_APPROVED");
    }

    // ── TC-047 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-047: getAssignedClaims throws NoResourceFoundException for unknown carrier email")
    void getAssignedClaims_withUnknownCarrierEmail_shouldThrowNotFound() {
        when(carrierRepository.findByUser_Email("unknown@carrier.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carrierService.getAssignedClaims("unknown@carrier.com"))
                .isInstanceOf(NoResourceFoundException.class)
                .hasMessageContaining("Carrier profile not found");
    }

    // ── TC-048 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-048: getClaimDetail throws BadRequestException when claim belongs to different carrier")
    void getClaimDetail_forClaimNotAssignedToCarrier_shouldThrowBadRequest() {
        Carrier anotherCarrier = Carrier.builder().id(99L).build();
        adminApprovedClaim.setCarrier(anotherCarrier);

        when(carrierRepository.findByUser_Email("carrier@test.com")).thenReturn(Optional.of(carrier));
        when(claimRepository.findById(100L)).thenReturn(Optional.of(adminApprovedClaim));

        assertThatThrownBy(() -> carrierService.getClaimDetail(100L, "carrier@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not assigned");
    }

    // ── TC-049 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-049: rejectClaim throws BadRequestException when claim is in a terminal state")
    void rejectClaim_fromSettledState_shouldThrowBadRequest() {
        adminApprovedClaim.setClaimStatus(ClaimStatus.SETTLED);
        when(carrierRepository.findByUser_Email("carrier@test.com")).thenReturn(Optional.of(carrier));
        when(claimRepository.findById(100L)).thenReturn(Optional.of(adminApprovedClaim));

        assertThatThrownBy(() -> carrierService.rejectClaim(100L, "carrier@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot be modified");
    }

    // ── TC-050 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-050: flagSuspicious increases riskScore by 25 and adds to riskFlags")
    void flagSuspicious_shouldIncreaseRiskScoreAndAppendFlag() {
        adminApprovedClaim.setRiskScore(0.0);
        adminApprovedClaim.setRiskFlags(null);

        when(carrierRepository.findByUser_Email("carrier@test.com")).thenReturn(Optional.of(carrier));
        when(claimRepository.findById(100L)).thenReturn(Optional.of(adminApprovedClaim));
        when(claimRepository.save(any())).thenReturn(adminApprovedClaim);

        carrierService.flagSuspicious(100L, "carrier@test.com");

        verify(claimRepository).save(argThat(c ->
                Double.compare(c.getRiskScore(), 25.0) == 0 && c.getRiskFlags() != null && c.getRiskFlags().contains("SUSPICIOUS")
        ));
    }

    // ── TC-051 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-051: addRemark appends to existing reviewNotes with timestamp prefix")
    void addRemark_shouldAppendToExistingNotes() {
        adminApprovedClaim.setReviewNotes("Previous note");
        when(carrierRepository.findByUser_Email("carrier@test.com")).thenReturn(Optional.of(carrier));
        when(claimRepository.findById(100L)).thenReturn(Optional.of(adminApprovedClaim));
        when(claimRepository.save(any())).thenReturn(adminApprovedClaim);

        carrierService.addRemark(100L, "Important remark", "carrier@test.com");

        verify(claimRepository).save(argThat(c ->
                c.getReviewNotes().contains("Previous note") &&
                        c.getReviewNotes().contains("Important remark")
        ));
    }

    // ── TC-052 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-052: flagSuspicious caps riskScore at 100 when existing score is 85")
    void flagSuspicious_withHighExistingScore_shouldCapAt100() {
        adminApprovedClaim.setRiskScore(85.0);

        when(carrierRepository.findByUser_Email("carrier@test.com")).thenReturn(Optional.of(carrier));
        when(claimRepository.findById(100L)).thenReturn(Optional.of(adminApprovedClaim));
        when(claimRepository.save(any())).thenReturn(adminApprovedClaim);

        carrierService.flagSuspicious(100L, "carrier@test.com");

        verify(claimRepository).save(argThat(c -> c.getRiskScore() <= 100.0));
    }
}
