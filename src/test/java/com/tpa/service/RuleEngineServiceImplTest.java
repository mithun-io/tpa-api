package com.tpa.service;

import com.tpa.dto.request.ClaimDataRequest;
import com.tpa.dto.response.ClaimDecisionResponse;
import com.tpa.entity.RuleConfig;
import com.tpa.enums.ClaimStatus;
import com.tpa.repository.RuleConfigRepository;
import com.tpa.repository.RuleExecutionAuditRepository;
import com.tpa.service.impl.RuleEngineServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleEngineServiceImplTest {

    @Mock
    private KieContainer kieContainer;

    @Mock
    private KieSession kieSession;

    @Mock
    private RuleConfigRepository ruleConfigRepository;

    @Mock
    private RuleExecutionAuditRepository auditRepository;

    @InjectMocks
    private RuleEngineServiceImpl ruleEngineService;

    @BeforeEach
    void setUp() {
        lenient().when(kieContainer.newKieSession()).thenReturn(kieSession);
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
                if (claim.claimFormAdmissionDate != null && claim.combinedDocAdmissionDate != null && claim.claimFormAdmissionDate != claim.combinedDocAdmissionDate) {
                    decision.status = review
                    decision.reasons.add('Admission date mismatch across documents')
                }
                if (claim.claimFormDischargeDate != null && claim.combinedDocDischargeDate != null && claim.claimFormDischargeDate != claim.combinedDocDischargeDate) {
                    decision.status = review
                    decision.reasons.add('Discharge date mismatch across documents')
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

    private ClaimDataRequest buildFullyValidRequest() {
        return ClaimDataRequest.builder()
                .claimFormPresent(true)
                .combinedDocumentPresent(true)
                .policyNumber("POL-SMOKE-01")
                .policyStatus("ACTIVE")
                .claimedAmount(3000.0)
                .totalBillAmount(4000.0)
                .isDuplicate(false)
                .claimFormPatientName("Test Patient")
                .combinedDocPatientName("Test Patient")
                .claimFormHospitalName("Test Hospital")
                .combinedDocHospitalName("Test Hospital")
                .claimFormAdmissionDate(LocalDate.of(2026, 1, 1))
                .combinedDocAdmissionDate(LocalDate.of(2026, 1, 1))
                .claimFormDischargeDate(LocalDate.of(2026, 1, 5))
                .combinedDocDischargeDate(LocalDate.of(2026, 1, 5))
                .build();
    }

    @Test
    public void testEvaluateClaim_Approved() {
        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(buildFullyValidRequest());

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.AI_VALIDATED);
        assertThat(response.getReasons()).contains("System auto-verified: Pending admin approval");
    }

    @Test
    public void testEvaluateClaim_Rejected_WhenClaimFormMissing() {
        ClaimDataRequest request = buildFullyValidRequest();
        request.setClaimFormPresent(false);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(response.getReasons()).contains("Claim form is missing");
    }

    @Test
    public void testEvaluateClaim_Review_WhenPolicyNumberMissing() {
        ClaimDataRequest request = buildFullyValidRequest();
        request.setPolicyNumber(null);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Policy number is missing");
    }
    @Test
    public void testEvaluateClaim_Review_WhenDuplicateClaim() {
        ClaimDataRequest request = buildFullyValidRequest();
        request.setIsDuplicate(true);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Possible duplicate claim detected");
    }

    @Test
    public void testEvaluateClaim_Review_WhenNamesMismatch() {
        ClaimDataRequest request = buildFullyValidRequest();
        request.setClaimFormPatientName("John");
        request.setCombinedDocPatientName("Doe");

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Patient name mismatch across documents");
    }

    @Test
    public void testEvaluateClaim_Review_WhenDatesMismatch() {
        ClaimDataRequest request = buildFullyValidRequest();
        request.setClaimFormAdmissionDate(LocalDate.of(2026, 1, 1));
        request.setCombinedDocAdmissionDate(LocalDate.of(2026, 1, 2));

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Admission date mismatch across documents");
    }

    @Test
    public void testEvaluateClaim_Review_WhenDischargeBeforeAdmission() {
        ClaimDataRequest request = buildFullyValidRequest();
        request.setClaimFormDischargeDate(LocalDate.of(2025, 1, 1)); // Before admission 2026

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("Discharge date mismatch across documents");
    }

    @Test
    public void testEvaluateClaim_Review_WhenHighAmount() {
        ClaimDataRequest request = buildFullyValidRequest();
        request.setClaimedAmount(55000.0);

        ClaimDecisionResponse response = ruleEngineService.evaluateClaim(request);

        assertThat(response.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(response.getReasons()).contains("High claim amount (> INR 50,000)");
    }
}

