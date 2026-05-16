package com.tpa.security;

import com.tpa.entity.User;
import com.tpa.enums.Gender;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-001 to TC-007: JwtUtil Unit Tests
 * Tests JWT token generation, validation, claim extraction, and expiry handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil - JWT Token Tests")
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private CustomUserDetails adminUserDetails;
    private CustomUserDetails patientUserDetails;

    @BeforeEach
    void setUp() {
        // Inject test values via ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret",
                "dGVzdFNlY3JldEtleUZvclRwYUFwcGxpY2F0aW9uVGVzdGluZzEyMzQ1Njc4OTA=");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 86400000L);

        User admin = User.builder()
                .id(1L)
                .username("Admin User")
                .email("admin@test.com")
                .phoneNumber("+12025550001")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .address("Admin HQ")
                .password("encodedPass")
                .userRole(UserRole.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        User patient = User.builder()
                .id(2L)
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
                .build();

        adminUserDetails = new CustomUserDetails(admin);
        patientUserDetails = new CustomUserDetails(patient);
    }

    // ── TC-001 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-001: generateToken produces non-null JWT with 3-part structure")
    void generateToken_shouldProduceValidJwtStructure() {
        String token = jwtUtil.generateToken(adminUserDetails);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    // ── TC-002 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-002: extractUsername returns the email used as subject")
    void extractUsername_shouldReturnCorrectSubject() {
        String token = jwtUtil.generateToken(adminUserDetails);
        String username = jwtUtil.extractUsername(token);

        assertThat(username).isEqualTo("admin@test.com");
    }

    // ── TC-003 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-003: extractRole returns ROLE_ADMIN for admin user")
    void extractRole_shouldReturnCorrectRoleForAdmin() {
        String token = jwtUtil.generateToken(adminUserDetails);
        String role = jwtUtil.extractRole(token);

        assertThat(role).isEqualTo("ROLE_ADMIN");
    }

    // ── TC-004 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-004: extractRole returns ROLE_PATIENT for patient user")
    void extractRole_shouldReturnCorrectRoleForPatient() {
        String token = jwtUtil.generateToken(patientUserDetails);
        String role = jwtUtil.extractRole(token);

        assertThat(role).isEqualTo("ROLE_PATIENT");
    }

    // ── TC-005 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-005: validateToken returns true for valid token and matching user")
    void validateToken_shouldReturnTrue_whenTokenMatchesUser() {
        String token = jwtUtil.generateToken(adminUserDetails);
        boolean valid = jwtUtil.validateToken(token, adminUserDetails);

        assertThat(valid).isTrue();
    }

    // ── TC-006 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-006: validateToken returns false when user does not match token subject")
    void validateToken_shouldReturnFalse_whenUserMismatch() {
        String token = jwtUtil.generateToken(adminUserDetails);
        boolean valid = jwtUtil.validateToken(token, patientUserDetails);

        assertThat(valid).isFalse();
    }

    // ── TC-007 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-007: isTokenExpired returns false for freshly generated token")
    void isTokenExpired_shouldReturnFalse_forFreshToken() {
        String token = jwtUtil.generateToken(adminUserDetails);
        boolean expired = jwtUtil.isTokenExpired(token);

        assertThat(expired).isFalse();
    }
}
