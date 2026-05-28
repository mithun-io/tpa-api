package com.tpa.repository;

import com.tpa.entity.User;
import com.tpa.enums.Gender;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import com.tpa.helper.AdminInitializer;
import com.tpa.helper.EnterpriseDataSeeder;
import org.kie.api.runtime.KieContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-077 to TC-082: UserRepository Integration Tests
 * Tests email/phone uniqueness checks, role-filtered queries,
 * search with pagination, and status-based lookups.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserRepository - Custom Query Tests")
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;

    @MockBean private AdminInitializer adminInitializer;
    @MockBean private EnterpriseDataSeeder enterpriseDataSeeder;
    @MockBean private KieContainer kieContainer;

    private User patientUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        patientUser = userRepository.save(User.builder()
                .username("John Patient")
                .email("patient@test.com")
                .phoneNumber("+12025551001")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .password("encodedPass")
                .gender(Gender.MALE)
                .userRole(UserRole.PATIENT)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build());

        adminUser = userRepository.save(User.builder()
                .username("Admin User")
                .email("admin@test.com")
                .phoneNumber("+12025550001")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .address("Admin HQ")
                .password("encodedPass")
                .gender(Gender.MALE)
                .userRole(UserRole.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build());
    }

    // ── TC-077 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-077: findByEmail returns user when email exists")
    void findByEmail_shouldReturnUser_whenEmailExists() {
        Optional<User> found = userRepository.findByEmail("patient@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("John Patient");
    }

    // ── TC-078 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-078: findByEmail returns empty when email does not exist")
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        Optional<User> found = userRepository.findByEmail("nonexistent@test.com");
        assertThat(found).isEmpty();
    }

    // ── TC-079 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-079: existsByEmail returns true for registered email")
    void existsByEmail_shouldReturnTrue_forExistingEmail() {
        boolean exists = userRepository.existsByEmail("admin@test.com");
        assertThat(exists).isTrue();
    }

    // ── TC-080 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-080: findByUserRole returns only PATIENT users")
    void findByUserRole_shouldReturnOnlyPatients() {
        Page<User> patients = userRepository.findByUserRole(
                UserRole.PATIENT,
                PageRequest.of(0, 10));

        assertThat(patients.getTotalElements()).isEqualTo(1);
        assertThat(patients.getContent().get(0).getUserRole()).isEqualTo(UserRole.PATIENT);
    }

    // ── TC-081 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-081: findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase performs case-insensitive search")
    void search_caseInsensitive_shouldReturnMatchingUsers() {
        Page<User> results = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                "john", "john",
                PageRequest.of(0, 10, Sort.by("createdAt").descending()));

        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getContent().get(0).getEmail()).isEqualTo("patient@test.com");
    }

    // ── TC-082 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-082: existsByEmailAndPhoneNumber returns true only when both match")
    void existsByEmailAndPhoneNumber_shouldReturnTrue_whenBothMatch() {
        boolean exactMatch = userRepository.existsByEmailAndPhoneNumber("patient@test.com", "+12025551001");
        boolean partialMatch = userRepository.existsByEmailAndPhoneNumber("patient@test.com", "+10000000000");

        assertThat(exactMatch).isTrue();
        assertThat(partialMatch).isFalse();
    }
}
