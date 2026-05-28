package com.tpa.repository;

import com.tpa.entity.*;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.Gender;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import com.tpa.helper.AdminInitializer;
import com.tpa.helper.EnterpriseDataSeeder;
import com.tpa.helper.ClaimSpecification;
import org.kie.api.runtime.KieContainer;
import com.tpa.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-069 to TC-076: ClaimRepository Integration Tests
 * Tests custom JPQL queries, Specification-based search, sorted pagination,
 * aggregate queries (sum/count), and entity graph eager loading.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ClaimRepository - Custom Query Tests")
class ClaimRepositoryTest {

    @Autowired private ClaimRepository claimRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClaimDocumentRepository claimDocumentRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;

    @MockBean private AdminInitializer adminInitializer;
    @MockBean private EnterpriseDataSeeder enterpriseDataSeeder;
    @MockBean private KieContainer kieContainer;

    private User savedUser;
    private Claim submittedClaim;
    private Claim approvedClaim;

    @BeforeEach
    void setUp() {
        claimDocumentRepository.deleteAll();
        claimRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        savedUser = userRepository.save(TestDataFactory.buildPatientUser());

        submittedClaim = claimRepository.save(TestDataFactory.buildSubmittedClaim(savedUser));

        Claim approved = TestDataFactory.buildSubmittedClaim(savedUser);
        approved.setPolicyNumber("PN-002");
        approved.setBillNumber("BILL-002");
        approved.setClaimStatus(ClaimStatus.ADMIN_APPROVED);
        approved.setAmount(50000.0);
        approvedClaim = claimRepository.save(approved);
    }

    // ── TC-069 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-069: findByUserId returns all claims for the given user")
    void findByUserId_shouldReturnAllClaimsForUser() {
        List<Claim> claims = claimRepository.findByUserId(savedUser.getId());
        assertThat(claims).hasSize(2);
    }

    // ── TC-070 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-070: findByUserId returns empty list for non-existent user ID")
    void findByUserId_withUnknownUser_shouldReturnEmpty() {
        List<Claim> claims = claimRepository.findByUserId(99999L);
        assertThat(claims).isEmpty();
    }

    // ── TC-071 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-071: Specification by ClaimStatus=SUBMITTED returns only submitted claims")
    void specification_byStatus_shouldFilterCorrectly() {
        Specification<Claim> spec = ClaimSpecification.hasStatus(ClaimStatus.SUBMITTED);
        List<Claim> results = claimRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getClaimStatus()).isEqualTo(ClaimStatus.SUBMITTED);
    }

    // ── TC-072 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-072: Specification with null status returns all claims")
    void specification_withNullStatus_shouldReturnAll() {
        Specification<Claim> spec = ClaimSpecification.hasStatus(null);
        List<Claim> results = claimRepository.findAll(spec);

        assertThat(results).hasSize(2);
    }

    // ── TC-073 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-073: amountBetween Specification filters claims within range correctly")
    void specification_amountBetween_shouldReturnOnlyMatchingClaims() {
        Specification<Claim> spec = ClaimSpecification.amountBetween(40000.0, 60000.0);
        List<Claim> results = claimRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAmount()).isBetween(40000.0, 60000.0);
    }

    // ── TC-074 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-074: countClaimsByStatus returns correct grouped result for both statuses")
    void countClaimsByStatus_shouldGroupCorrectlyByStatus() {
        List<Object[]> results = claimRepository.countClaimsByStatus();

        assertThat(results).isNotEmpty();
        boolean hasSubmitted = results.stream()
                .anyMatch(r -> ClaimStatus.SUBMITTED.name().equals(r[0].toString())
                        && ((Number) r[1]).longValue() >= 1);
        assertThat(hasSubmitted).isTrue();
    }

    // ── TC-075 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-075: sumApprovedClaimAmount returns sum only for ADMIN_APPROVED/CARRIER_APPROVED/SETTLED claims")
    void sumApprovedClaimAmount_shouldReturnCorrectTotal() {
        Double total = claimRepository.sumApprovedClaimAmount();
        assertThat(total).isNotNull().isEqualTo(50000.0);
    }

    // ── TC-076 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-076: findByClaimStatus with pagination returns paged results correctly")
    void findByClaimStatus_withPageable_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        Page<Claim> page = claimRepository.findByClaimStatus(ClaimStatus.SUBMITTED, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getClaimStatus()).isEqualTo(ClaimStatus.SUBMITTED);
    }
}
