package com.tpa.helper;

import com.tpa.enums.ClaimStatus;
import com.tpa.exception.ConflictException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * TC-008 to TC-015: ClaimStateMachine Unit Tests
 * Validates all legal and illegal state transitions in the insurance claim workflow.
 */
@DisplayName("ClaimStateMachine - State Transition Tests")
class ClaimStateMachineTest {

    private ClaimStateMachine claimStateMachine;

    @BeforeEach
    void setUp() {
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        claimStateMachine = new ClaimStateMachine(meterRegistry);
    }

    // ── TC-008 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-008: SUBMITTED → AI_VALIDATED is a valid transition")
    void validateTransition_submittedToAiValidated_shouldSucceed() {
        assertThatNoException()
                .isThrownBy(() -> claimStateMachine.validateTransition(ClaimStatus.SUBMITTED, ClaimStatus.AI_VALIDATED));
    }

    // ── TC-009 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-009: SUBMITTED → CARRIER_APPROVED is a valid transition")
    void validateTransition_submittedToCarrierApproved_shouldSucceed() {
        assertThatNoException()
                .isThrownBy(() -> claimStateMachine.validateTransition(ClaimStatus.SUBMITTED, ClaimStatus.CARRIER_APPROVED));
    }

    // ── TC-010 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-010: CARRIER_APPROVED → ADMIN_APPROVED is a valid transition")
    void validateTransition_carrierApprovedToAdminApproved_shouldSucceed() {
        assertThatNoException()
                .isThrownBy(() -> claimStateMachine.validateTransition(ClaimStatus.CARRIER_APPROVED, ClaimStatus.ADMIN_APPROVED));
    }

    // ── TC-011 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-011: ADMIN_APPROVED → SETTLED is a valid transition")
    void validateTransition_adminApprovedToSettled_shouldSucceed() {
        assertThatNoException()
                .isThrownBy(() -> claimStateMachine.validateTransition(ClaimStatus.ADMIN_APPROVED, ClaimStatus.SETTLED));
    }

    // ── TC-012 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-012: REJECTED → any status throws ConflictException (terminal state)")
    void validateTransition_rejectedToAnyStatus_shouldThrowConflict() {
        assertThatThrownBy(() -> claimStateMachine.validateTransition(ClaimStatus.REJECTED, ClaimStatus.SUBMITTED))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Invalid status transition");
    }

    // ── TC-013 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-013: SETTLED → any status throws ConflictException (terminal state)")
    void validateTransition_settledToAnyStatus_shouldThrowConflict() {
        assertThatThrownBy(() -> claimStateMachine.validateTransition(ClaimStatus.SETTLED, ClaimStatus.SUBMITTED))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Invalid status transition");
    }

    // ── TC-014 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-014: Same status transition throws ConflictException (idempotency guard)")
    void validateTransition_sameStatus_shouldThrowConflict() {
        assertThatThrownBy(() -> claimStateMachine.validateTransition(ClaimStatus.SUBMITTED, ClaimStatus.SUBMITTED))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already in");
    }

    // ── TC-015 ────────────────────────────────────────────────────────────────

    @ParameterizedTest
    @CsvSource({
            "SUBMITTED, SETTLED",
            "SUBMITTED, PAYMENT_PENDING",
            "AI_VALIDATED, SUBMITTED",
            "CARRIER_APPROVED, SUBMITTED"
    })
    @DisplayName("TC-015: Backward or illegal transitions throw ConflictException")
    void validateTransition_illegalTransitions_shouldThrowConflict(ClaimStatus from, ClaimStatus to) {
        assertThatThrownBy(() -> claimStateMachine.validateTransition(from, to))
                .isInstanceOf(ConflictException.class);
    }
}
