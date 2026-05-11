package com.tpa.service.impl;

import com.tpa.dto.request.ClaimDataRequest;
import com.tpa.dto.response.ClaimDecisionResponse;
import com.tpa.entity.RuleConfig;
import com.tpa.enums.ClaimStatus;
import com.tpa.repository.RuleConfigRepository;
import com.tpa.repository.RuleExecutionAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.KieContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleEngineServiceImplTest {

    @Mock
    private KieContainer kieContainer;

    @Mock
    private RuleConfigRepository ruleConfigRepository;

    @Mock
    private RuleExecutionAuditRepository auditRepository;

    @InjectMocks
    private RuleEngineServiceImpl ruleEngineService;

    @BeforeEach
    void setUp() {
        when(ruleConfigRepository.findByActiveTrueOrderByPriorityAsc()).thenReturn(List.of(legacyValidationRule()));
    }

    private RuleConfig legacyValidationRule() {
        String script = """
                def review = com.tpa.enums.ClaimStatus.UNDER_REVIEW
                if (claim.claimFormPresent == Boolean.FALSE) {
                    decision.status = com.tpa.enums.ClaimStatus.SUBMITTED
                    decision.reasons.add('Claim form is missing')
                    return true
                }
                if (claim.combinedDocumentPresent == Boolean.FALSE) {
                    decision.status = com.tpa.enums.ClaimStatus.SUBMITTED
                    decision.reasons.add('Combined document is missing')
                    return true
                }
                if (claim.policyStatus != null && !claim.policyStatus.equalsIgnoreCase('ACTIVE')) {
                    decision.status = com.tpa.enums.ClaimStatus.REJECTED
                    decision.reasons.add('Policy is inactive')
                    return true
                }
                if (claim.policyNumber == null || claim.policyNumber.isBlank()) {
                    decision.status = review
                    decision.reasons.add('Policy number is missing')
                }
                if (claim.isDuplicate == Boolean.TRUE) {
                    decision.status = review
                    decision.reasons.add('Possible duplicate claim detected')
                }
                if (claim.claimFormPatientName != null && claim.combinedDocPatientName != null && claim.claimFormPatientName != claim.combinedDocPatientName) {
                    decision.status = review
                    decision.reasons.add('Patient name mismatch across documents')
                }
                if (claim.claimFormHospitalName != null && claim.combinedDocHospitalName != null && claim.claimFormHospitalName != claim.combinedDocHospitalName) {
                    decision.status = review
                    decision.reasons.add('Hospital name mismatch across documents')
                }
                if (claim.claimFormAdmissionDate != null && claim.combinedDocAdmissionDate != null && claim.claimFormAdmissionDate != claim.combinedDocAdmissionDate) {
                    decision.status = review
                    decision.reasons.add('Admission date mismatch across documents')
                }
                if (claim.claimFormDischargeDate != null && claim.combinedDocDischargeDate != null && claim.claimFormDischargeDate != claim.combinedDocDischargeDate) {
                    decision.status = review
                    decision.reasons.add('Discharge date mismatch across documents')
                }
                if (claim.claimedAmount != null && claim.totalBillAmount != null && claim.claimedAmount > claim.totalBillAmount) {
                    decision.status = review
                    decision.reasons.add('Claimed amount is greater than total bill amount')
                }
                if (claim.claimedAmount != null && claim.claimedAmount > 50000) {
                    decision.status = review
                    decision.reasons.add('High claim amount (> INR 50,000)')
                }
                if (decision.status == null) {
                    decision.status = com.tpa.enums.ClaimStatus.AI_VALIDATED
                    decision.reasons.add('System auto-verified: Pending admin approval')
                }
                return true
                """;

        return RuleConfig.builder()
                .ruleKey("TEST_LEGACY_VALIDATION")
                .ruleType("GROOVY")
                .ruleValue("test")
                .groovyScript(script)
                .priority(1)
                .active(true)
                .build();
    }

    // ========== Helpers ==========

    private ClaimDataRequest buildValidRequest() {
        return ClaimDataRequest.builder()
                .claimFormPresent(true)
                .combinedDocumentPresent(true)
                .policyNumber("POL-001")
                .policyStatus("ACTIVE")
                .claimedAmount(5000.0)
                .totalBillAmount(6000.0)
                .isDuplicate(false)
                .claimFormPatientName("John Doe")
                .combinedDocPatientName("John Doe")
                .claimFormHospitalName("Apollo Hospitals")
                .combinedDocHospitalName("Apollo Hospitals")
                .claimFormAdmissionDate(LocalDate.of(2026, 1, 1))
                .combinedDocAdmissionDate(LocalDate.of(2026, 1, 1))
                .claimFormDischargeDate(LocalDate.of(2026, 1, 5))
                .combinedDocDischargeDate(LocalDate.of(2026, 1, 5))
                .build();
    }

    // ========== Positive Cases ==========

    @Test
    void evaluateClaim_shouldReturnAiValidated_whenAllRulesPass() {
        ClaimDataRequest request = buildValidRequest();

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.AI_VALIDATED);
        assertThat(response.getReasons()).contains("System auto-verified: Pending admin approval");
    }

    // ========== Negative Cases — Hard Reject ==========

    @Test
    void evaluateClaim_shouldReturnPending_whenClaimFormMissing() {
        ClaimDataRequest request = buildValidRequest();
        request.setClaimFormPresent(false);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(response.getReasons()).contains("Claim form is missing");
    }

    @Test
    void evaluateClaim_shouldReturnPending_whenCombinedDocumentMissing() {
        ClaimDataRequest request = buildValidRequest();
        request.setCombinedDocumentPresent(false);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(response.getReasons()).contains("Combined document is missing");
    }

    @Test
    void evaluateClaim_shouldReturnRejected_whenPolicyIsInactive() {
        ClaimDataRequest request = buildValidRequest();
        request.setPolicyStatus("INACTIVE");

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.REJECTED);
        assertThat(response.getReasons()).contains("Policy is inactive");
    }

    // ========== Review Cases ==========

    @Test
    void evaluateClaim_shouldReturnReview_whenPatientNameMismatch() {
        ClaimDataRequest request = buildValidRequest();
        request.setCombinedDocPatientName("Jane Doe"); // mismatch

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Patient name mismatch across documents");
    }

    @Test
    void evaluateClaim_shouldReturnReview_whenHospitalNameMismatch() {
        ClaimDataRequest request = buildValidRequest();
        request.setCombinedDocHospitalName("City Hospital"); // mismatch

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Hospital name mismatch across documents");
    }

    @Test
    void evaluateClaim_shouldReturnReview_whenAdmissionDateMismatch() {
        ClaimDataRequest request = buildValidRequest();
        request.setCombinedDocAdmissionDate(LocalDate.of(2026, 1, 2)); // mismatch

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Admission date mismatch across documents");
    }

    @Test
    void evaluateClaim_shouldReturnReview_whenClaimedAmountExceedsBillAmount() {
        ClaimDataRequest request = buildValidRequest();
        request.setClaimedAmount(10000.0);
        request.setTotalBillAmount(5000.0);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Claimed amount is greater than total bill amount");
    }

    @Test
    void evaluateClaim_shouldReturnReview_whenClaimAmountIsAbove50000() {
        ClaimDataRequest request = buildValidRequest();
        request.setClaimedAmount(55000.0);
        request.setTotalBillAmount(60000.0);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("High claim amount (> INR 50,000)");
    }

    // ========== Edge Cases ==========

    @Test
    void evaluateClaim_shouldReturnReview_whenDuplicateClaimDetected() {
        ClaimDataRequest request = buildValidRequest();
        request.setIsDuplicate(true);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Possible duplicate claim detected");
    }

    @Test
    void evaluateClaim_shouldReturnReview_whenPolicyNumberIsMissing() {
        ClaimDataRequest request = buildValidRequest();
        request.setPolicyNumber(null);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Policy number is missing");
    }

    @Test
    void evaluateClaim_shouldAccumulateMultipleReasons_whenSeveralRulesViolated() {
        ClaimDataRequest request = buildValidRequest();
        request.setIsDuplicate(true);
        request.setCombinedDocPatientName("Wrong Name");
        request.setCombinedDocHospitalName("Wrong Hospital");

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).hasSizeGreaterThanOrEqualTo(3);
        assertThat(response.getReasons()).contains(
                "Possible duplicate claim detected",
                "Patient name mismatch across documents",
                "Hospital name mismatch across documents"
        );
    }
}
