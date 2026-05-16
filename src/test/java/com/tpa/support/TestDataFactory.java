package com.tpa.support;

import com.tpa.dto.request.auth.LoginRequest;
import com.tpa.dto.request.claim.ClaimQueryRequest;
import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.request.claim.ClaimReviewRequest;
import com.tpa.entity.*;
import com.tpa.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Centralized factory for test fixtures.
 * Ensures test data consistency across the entire suite.
 */
public final class TestDataFactory {

    private TestDataFactory() {}

    // ── User Fixtures ─────────────────────────────────────────────────────────

    public static User buildPatientUser() {
        return User.builder()
                .username("John Patient")
                .email("patient@test.com")
                .phoneNumber("+12025551001")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St, New York, NY 10001")
                .password("$2a$10$encodedPasswordHash")
                .gender(Gender.MALE)
                .userRole(UserRole.PATIENT)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User buildAdminUser() {
        return User.builder()
                .username("Admin User")
                .email("admin@test.com")
                .phoneNumber("+12025550001")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .address("Admin Headquarters, Washington DC")
                .password("$2a$10$encodedPasswordHash")
                .gender(Gender.MALE)
                .userRole(UserRole.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User buildCarrierUser() {
        return User.builder()
                .username("ABC Insurance Corp")
                .email("carrier@test.com")
                .phoneNumber("+12025552001")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("100 Insurance Ave, Chicago, IL 60601")
                .password("$2a$10$encodedPasswordHash")
                .gender(null)
                .userRole(UserRole.CARRIER)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User buildInactiveCarrierUser() {
        User user = buildCarrierUser();
        user.setUserStatus(UserStatus.INACTIVE);
        return user;
    }

    // ── Carrier Fixtures ──────────────────────────────────────────────────────

    public static Carrier buildCarrier(User user) {
        return Carrier.builder()
                .user(user)
                .companyName("ABC Insurance Corp")
                .companyType("Health Insurance")
                .registrationNumber("REG-2024-001")
                .licenseNumber("LIC-001")
                .taxId("TAX-001")
                .website("https://abcinsurance.com")
                .build();
    }

    // ── Claim Fixtures ────────────────────────────────────────────────────────

    public static Claim buildSubmittedClaim(User user) {
        return Claim.builder()
                .user(user)
                .patientName("John Doe")
                .hospitalName("City General Hospital")
                .admissionDate(LocalDate.of(2024, 1, 10))
                .dischargeDate(LocalDate.of(2024, 1, 15))
                .totalBillAmount(25000.0)
                .policyId("POL-2024-001")
                .policyNumber("PN-001")
                .policyName("Comprehensive Health Plan")
                .claimStatus(ClaimStatus.SUBMITTED)
                .claimType("Hospitalization")
                .diagnosis("Appendicitis")
                .billNumber("BILL-2024-001")
                .amount(25000.0)
                .billDate(LocalDate.of(2024, 1, 15))
                .riskScore(0.0)
                .escalated(false)
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static Claim buildAdminApprovedClaim(User user, Carrier carrier) {
        Claim claim = buildSubmittedClaim(user);
        claim.setCarrier(carrier);
        claim.setClaimStatus(ClaimStatus.ADMIN_APPROVED);
        claim.setReviewedBy("admin@test.com");
        claim.setReviewedAt(LocalDateTime.now());
        return claim;
    }

    public static Claim buildCarrierApprovedClaim(User user, Carrier carrier) {
        Claim claim = buildAdminApprovedClaim(user, carrier);
        claim.setClaimStatus(ClaimStatus.CARRIER_APPROVED);
        return claim;
    }

    public static Claim buildRejectedClaim(User user) {
        Claim claim = buildSubmittedClaim(user);
        claim.setClaimStatus(ClaimStatus.REJECTED);
        claim.setRejectionReason("Claim documentation insufficient");
        return claim;
    }

    // ── ClaimRequest DTO Fixtures ─────────────────────────────────────────────

    public static ClaimRequest buildValidClaimRequest() {
        return ClaimRequest.builder()
                .policyId("POL-2024-001")
                .policyName("Comprehensive Health Plan")
                .policyNumber("PN-001")
                .policyStatus(PolicyStatus.ACTIVE)
                .claimFormPresent(true)
                .claimFormPatientName("John Doe")
                .claimFormHospitalName("City General Hospital")
                .claimFormAdmissionDate(LocalDate.of(2024, 1, 10))
                .claimFormDischargeDate(LocalDate.of(2024, 1, 15))
                .combinedDocumentPresent(true)
                .combinedDocPatientName("John Doe")
                .combinedDocHospitalName("City General Hospital")
                .combinedDocAdmissionDate(LocalDate.of(2024, 1, 10))
                .combinedDocDischargeDate(LocalDate.of(2024, 1, 15))
                .claimedAmount(3000.0)
                .totalBillAmount(25000.0)
                .carrierName("ABC Insurance Corp")
                .claimType("Hospitalization")
                .diagnosis("Appendicitis")
                .billNumber("BILL-2024-001")
                .billDate(LocalDate.of(2024, 1, 15))
                .isDuplicate(false)
                .build();
    }

    public static ClaimRequest buildHighAmountClaimRequest() {
        ClaimRequest req = buildValidClaimRequest();
        req.setClaimedAmount(75000.0);
        req.setTotalBillAmount(75000.0);
        return req;
    }

    public static ClaimRequest buildInactivePolicyClaimRequest() {
        ClaimRequest req = buildValidClaimRequest();
        req.setPolicyStatus(PolicyStatus.INACTIVE);
        return req;
    }

    // ── Auth DTO Fixtures ─────────────────────────────────────────────────────

    public static LoginRequest buildLoginRequest(String email, String password) {
        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }

    // ── RuleConfig Fixtures ───────────────────────────────────────────────────

    public static RuleConfig buildActiveSimpleRule(String ruleKey, String ruleValue, int priority) {
        return RuleConfig.builder()
                .ruleKey(ruleKey)
                .ruleValue(ruleValue)
                .description("Test rule: " + ruleKey)
                .ruleType("SIMPLE")
                .priority(priority)
                .version(1)
                .active(true)
                .simulationMode(false)
                .category("AMOUNT")
                .lastUpdatedBy("test-admin")
                .build();
    }

    public static RuleConfig buildInactiveRule(String ruleKey) {
        return RuleConfig.builder()
                .ruleKey(ruleKey)
                .ruleValue("0")
                .ruleType("SIMPLE")
                .priority(99)
                .version(1)
                .active(false)
                .simulationMode(false)
                .category("DEFAULT")
                .build();
    }

    // ── Payment Fixtures ──────────────────────────────────────────────────────

    public static Payment buildSuccessPayment(Long claimId, Long userId) {
        return Payment.builder()
                .claimId(claimId)
                .userId(userId)
                .amount(25000.0)
                .currency("INR")
                .status(PaymentStatus.SUCCESS)
                .razorpayOrderId("order_test_001")
                .razorpayPaymentId("pay_test_001")
                .build();
    }

    // ── Claim Review Request ──────────────────────────────────────────────────

    public static ClaimReviewRequest buildReviewRequest(Long claimId, ClaimStatus status, String notes) {
        ClaimReviewRequest req = new ClaimReviewRequest();
        req.setClaimId(claimId);
        req.setClaimStatus(status);
        req.setReviewNotes(notes);
        return req;
    }

    // ── Claim Query Request ───────────────────────────────────────────────────

    public static ClaimQueryRequest buildClaimQueryRequest(String message) {
        ClaimQueryRequest req = new ClaimQueryRequest();
        req.setMessage(message);
        return req;
    }
}
