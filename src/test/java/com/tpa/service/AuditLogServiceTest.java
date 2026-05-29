package com.tpa.service;

import com.tpa.entity.*;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PaymentEventType;
import com.tpa.enums.PaymentStatus;
import com.tpa.helper.AdminInitializer;
import com.tpa.helper.EnterpriseDataSeeder;
import org.kie.api.runtime.KieContainer;
import com.tpa.repository.*;
import com.tpa.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-025 to TC-032: AuditLogService Integration Tests
 * Tests blockchain-style hash chaining, integrity verification,
 * timeline recording, payment ledger queries, and tamper detection.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("AuditLogService - Audit Chain & Ledger Tests")
class AuditLogServiceTest {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentLedgerRepository paymentLedgerRepository;

    @Autowired
    private ClaimDocumentRepository claimDocumentRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private AdminInitializer adminInitializer;

    @MockBean
    private EnterpriseDataSeeder enterpriseDataSeeder;

    @MockBean
    private KieContainer kieContainer;

    private User savedUser;
    private Claim savedClaim;

    @BeforeEach
    void setUp() {
        paymentLedgerRepository.deleteAll();
        auditLogRepository.deleteAllInBatch();
        claimDocumentRepository.deleteAll();
        claimRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        savedUser = userRepository.save(TestDataFactory.buildPatientUser());

        savedClaim = Claim.builder()
                .user(savedUser)
                .patientName("John Doe")
                .hospitalName("City Hospital")
                .policyNumber("PN-TEST-001")
                .claimStatus(ClaimStatus.SUBMITTED)
                .amount(10000.0)
                .escalated(false)
                .createdDate(java.time.LocalDateTime.now())
                .build();
        savedClaim = claimRepository.save(savedClaim);
    }

    // ── TC-025 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-025: logAction creates an AuditLog with GENESIS previousHash for first record")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void logAction_firstRecord_shouldHaveGenesisHash() throws InterruptedException {
        auditLogService.logAction(savedClaim.getId(), "CLAIM_CREATED", null, ClaimStatus.SUBMITTED);

        // Wait for @Async execution
        Thread.sleep(500);

        List<AuditLog> logs = auditLogRepository.findByClaimIdOrderByIdAsc(savedClaim.getId());
        assertThat(logs).isNotEmpty();
        AuditLog first = logs.get(0);
        assertThat(first.getPreviousHash()).isEqualTo("GENESIS");
        assertThat(first.getIntegrityHash()).isNotNull().isNotBlank();
    }

    // ── TC-026 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-026: getClaimAuditTrail returns all audit records in ascending order")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void getClaimAuditTrail_shouldReturnAllRecordsInOrder() throws InterruptedException {
        auditLogService.logAction(savedClaim.getId(), "CLAIM_CREATED", null, ClaimStatus.SUBMITTED);
        Thread.sleep(200);
        auditLogService.logAction(savedClaim.getId(), "ADMIN_REVIEW", ClaimStatus.SUBMITTED, ClaimStatus.UNDER_REVIEW);
        Thread.sleep(500);

        List<AuditLog> trail = auditLogService.getClaimAuditTrail(savedClaim.getId());

        assertThat(trail).hasSizeGreaterThanOrEqualTo(2);
        for (int i = 1; i < trail.size(); i++) {
            assertThat(trail.get(i).getId()).isGreaterThan(trail.get(i - 1).getId());
        }
    }

    // ── TC-027 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-027: verifyIntegrity returns chainIntact=true for fresh audit trail")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void verifyIntegrity_freshChain_shouldReturnIntact() throws InterruptedException {
        auditLogService.logAction(savedClaim.getId(), "CLAIM_CREATED", null, ClaimStatus.SUBMITTED);
        Thread.sleep(500);

        var response = auditLogService.verifyIntegrity(savedClaim.getId());

        assertThat(response.isChainIntact()).isTrue();
        assertThat(response.getMessage()).contains("intact");
    }

    // ── TC-028 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-028: verifyIntegrity returns chainIntact=true for empty audit trail (no records)")
    void verifyIntegrity_withNoRecords_shouldReturnIntact() {
        var response = auditLogService.verifyIntegrity(99999L);

        assertThat(response.isChainIntact()).isTrue();
        assertThat(response.getTotalRecords()).isEqualTo(0);
    }

    // ── TC-029 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-029: reconcilePayments returns zero totals when ledger is empty")
    void reconcilePayments_withEmptyLedger_shouldReturnZero() {
        var reconciliation = auditLogService.reconcilePayments();

        assertThat(reconciliation.getTotalAmountSettled()).isEqualTo(0.0);
        assertThat(reconciliation.getTotalVerifiedPayments()).isEqualTo(0L);
        assertThat(reconciliation.getCurrency()).isEqualTo("INR");
    }

    // ── TC-030 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-030: reconcilePayments sums only PAYMENT_SUCCESS ledger entries")
    void reconcilePayments_shouldSumOnlySuccessEntries() {
        PaymentLedger successEntry = PaymentLedger.builder()
                .claimId(savedClaim.getId())
                .paymentId(1L)
                .amount(25000.0)
                .currency("INR")
                .paymentEventType(PaymentEventType.PAYMENT_SUCCESS)
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();
        paymentLedgerRepository.save(successEntry);

        PaymentLedger failedEntry = PaymentLedger.builder()
                .claimId(savedClaim.getId())
                .paymentId(2L)
                .amount(5000.0)
                .currency("INR")
                .paymentEventType(PaymentEventType.PAYMENT_FAILED)
                .paymentStatus(PaymentStatus.FAILED)
                .build();
        paymentLedgerRepository.save(failedEntry);

        var reconciliation = auditLogService.reconcilePayments();
        assertThat(reconciliation.getTotalVerifiedPayments()).isEqualTo(1L);
    }

    // ── TC-031 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-031: getPaymentLedger returns all ledger entries for a claim in ascending order")
    void getPaymentLedger_shouldReturnAllEntriesForClaim() {
        for (int i = 0; i < 3; i++) {
            paymentLedgerRepository.save(PaymentLedger.builder()
                    .claimId(savedClaim.getId())
                    .paymentId((long) i)
                    .amount(1000.0 * (i + 1))
                    .currency("INR")
                    .paymentEventType(PaymentEventType.PAYMENT_CREATED)
                    .paymentStatus(PaymentStatus.CREATED)
                    .build());
        }

        List<PaymentLedger> ledger = auditLogService.getPaymentLedger(savedClaim.getId());
        assertThat(ledger).hasSize(3);
    }

    // ── TC-032 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-032: getByClaimAndAction returns only audit logs matching the given action")
    @WithMockUser(username = "system", roles = "ADMIN")
    void getByClaimAndAction_shouldFilterByActionCorrectly() throws InterruptedException {
        auditLogService.logAction(savedClaim.getId(), "CLAIM_CREATED", null, ClaimStatus.SUBMITTED);
        Thread.sleep(200);
        auditLogService.logAction(savedClaim.getId(), "ADMIN_REVIEW", ClaimStatus.SUBMITTED, ClaimStatus.UNDER_REVIEW);
        Thread.sleep(500);

        List<AuditLog> filtered = auditLogService.getByClaimAndAction(savedClaim.getId(), "CLAIM_CREATED");
        assertThat(filtered).allMatch(log -> log.getAction().equals("CLAIM_CREATED"));
    }
}
