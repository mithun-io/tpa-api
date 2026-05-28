package com.tpa.service;

import com.tpa.entity.*;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PaymentStatus;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.PaymentLedgerRepository;
import com.tpa.repository.PaymentRepository;
import com.tpa.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TC-053 to TC-060: PaymentService Unit Tests
 * Tests payment eligibility validation, duplicate payment prevention,
 * signature verification, instant payout flow, and ledger persistence.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService - Payment Processing Unit Tests")
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private ClaimRepository claimRepository;
    @Mock private PaymentLedgerRepository paymentLedgerRepository;

    @InjectMocks
    private com.tpa.service.impl.PaymentServiceImpl paymentService;

    private User patientUser;
    private Carrier carrier;
    private Claim carrierApprovedClaim;
    private Claim settledClaim;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "razorpayKey", "test_key");
        ReflectionTestUtils.setField(paymentService, "razorpaySecret", "test_secret_1234567890");

        patientUser = TestDataFactory.buildPatientUser();
        patientUser.setId(1L);

        carrier = TestDataFactory.buildCarrier(patientUser);
        carrier.setId(5L);

        carrierApprovedClaim = TestDataFactory.buildCarrierApprovedClaim(patientUser, carrier);
        carrierApprovedClaim.setId(100L);

        settledClaim = TestDataFactory.buildCarrierApprovedClaim(patientUser, carrier);
        settledClaim.setId(200L);
        settledClaim.setClaimStatus(ClaimStatus.SETTLED);
    }

    // ── TC-053 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-053: createOrder throws IllegalStateException for claim not in approved state")
    void createOrder_withSubmittedClaim_shouldThrowIllegalState() {
        Claim submittedClaim = TestDataFactory.buildSubmittedClaim(patientUser);
        submittedClaim.setId(50L);
        when(claimRepository.findById(50L)).thenReturn(Optional.of(submittedClaim));

        com.tpa.dto.request.payment.CreatePaymentOrderRequest req =
                new com.tpa.dto.request.payment.CreatePaymentOrderRequest(50L, 25000.0);

        assertThatThrownBy(() -> paymentService.createOrder(1L, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payment allowed only for approved claims");
    }

    // ── TC-054 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-054: createOrder throws IllegalStateException for already successful payment")
    void createOrder_withDuplicateSuccessfulPayment_shouldThrowIllegalState() {
        when(claimRepository.findById(100L)).thenReturn(Optional.of(carrierApprovedClaim));

        Payment existing = TestDataFactory.buildSuccessPayment(100L, 1L);
        when(paymentRepository.findByClaimId(100L)).thenReturn(Optional.of(existing));

        com.tpa.dto.request.payment.CreatePaymentOrderRequest req =
                new com.tpa.dto.request.payment.CreatePaymentOrderRequest(100L, 25000.0);

        assertThatThrownBy(() -> paymentService.createOrder(1L, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payment already completed");
    }

    // ── TC-055 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-055: verifyPayment throws SecurityException for invalid signature")
    void verifyPayment_withInvalidSignature_shouldThrowSecurityException() {
        Payment payment = TestDataFactory.buildSuccessPayment(100L, 1L);
        payment.setStatus(PaymentStatus.CREATED);
        payment.setRazorpayOrderId("order_123");
        when(paymentRepository.findByRazorpayOrderId("order_123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenReturn(payment);

        com.tpa.dto.request.payment.VerifyPaymentRequest req =
                new com.tpa.dto.request.payment.VerifyPaymentRequest(
                        "order_123", "pay_123", "invalid_signature");

        assertThatThrownBy(() -> paymentService.verifyPayment(req))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("signature verification failed");
    }

    // ── TC-056 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-056: getPaymentByClaimId throws NoResourceFoundException when no payment exists")
    void getPaymentByClaimId_withNonExistentPayment_shouldThrowNotFound() {
        when(paymentRepository.findByClaimId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentByClaimId(999L))
                .isInstanceOf(NoResourceFoundException.class)
                .hasMessageContaining("No payment found");
    }

    // ── TC-057 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-057: initiateInstantPayout creates SUCCESS payment and sets claim to SETTLED")
    void initiateInstantPayout_shouldCreateSuccessPaymentAndSettleClaim() {
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(claimRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(paymentLedgerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        paymentService.initiateInstantPayout(carrierApprovedClaim);

        verify(paymentRepository).save(argThat(p ->
                p.getStatus() == PaymentStatus.SUCCESS &&
                        p.getRazorpayOrderId().startsWith("MOCK_INSTANT_")
        ));
        verify(claimRepository).save(argThat(c -> c.getClaimStatus() == ClaimStatus.SETTLED));
    }

    // ── TC-058 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-058: initiateInstantPayout saves a PaymentLedger entry with PAYMENT_SUCCESS event type")
    void initiateInstantPayout_shouldSaveLedgerEntryWithSystemInitiator() {
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(claimRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(paymentLedgerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        paymentService.initiateInstantPayout(carrierApprovedClaim);

        verify(paymentLedgerRepository).save(argThat(l ->
                l.getInitiatedBy().equals("SYSTEM") &&
                        l.getPaymentStatus() == PaymentStatus.SUCCESS
        ));
    }

    // ── TC-059 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-059: createOrder throws NoResourceFoundException when claim not found")
    void createOrder_withNonExistentClaim_shouldThrowNotFound() {
        when(claimRepository.findById(999L)).thenReturn(Optional.empty());

        com.tpa.dto.request.payment.CreatePaymentOrderRequest req =
                new com.tpa.dto.request.payment.CreatePaymentOrderRequest(999L, 25000.0);

        assertThatThrownBy(() -> paymentService.createOrder(1L, req))
                .isInstanceOf(NoResourceFoundException.class)
                .hasMessageContaining("Claim not found");
    }

    // ── TC-060 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-060: getPaymentByClaimId returns correct PaymentResponse when payment exists")
    void getPaymentByClaimId_withExistingPayment_shouldReturnResponse() {
        Payment existing = TestDataFactory.buildSuccessPayment(100L, 1L);
        existing.setId(50L);
        when(paymentRepository.findByClaimId(100L)).thenReturn(Optional.of(existing));

        var response = paymentService.getPaymentByClaimId(100L);

        assertThat(response).isNotNull();
        assertThat(response.claimId()).isEqualTo(100L);
        assertThat(response.status()).isEqualTo(PaymentStatus.SUCCESS);
    }
}
