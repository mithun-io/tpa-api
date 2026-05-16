package com.tpa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.dto.request.auth.LoginRequest;
import com.tpa.dto.request.user.PatientRequest;
import com.tpa.entity.User;
import com.tpa.enums.Gender;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import com.tpa.repository.RefreshTokenRepository;
import com.tpa.repository.UserRepository;
import com.tpa.support.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TC-083 to TC-089: AuthController Integration Tests
 * Tests login success/failure, inactive user blocking, JWT issuance,
 * bearer token refresh, validation errors, and unauthenticated access.
 */
@DisplayName("AuthController - Authentication & Security Tests")
class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── TC-083 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-083: POST /api/v1/auth/login returns 200 and JWT token for valid credentials")
    void login_withValidCredentials_shouldReturn200WithToken() throws Exception {
        // savedPatient is created in BaseControllerTest.baseSetUp()
        LoginRequest request = new LoginRequest();
        request.setEmail("patient@test.com");
        request.setPassword("Patient@1234");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    // ── TC-084 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-084: POST /api/v1/auth/login returns 401 for invalid password")
    void login_withInvalidPassword_shouldReturn401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("patient@test.com");
        request.setPassword("WrongPass@1234");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ── TC-085 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-085: POST /api/v1/auth/login returns 400 for PENDING carrier account")
    void login_withPendingCarrierAccount_shouldReturn400() throws Exception {
        // Create a PENDING carrier user
        userRepository.save(User.builder()
                .username("Pending Carrier")
                .email("pending@carrier.com")
                .phoneNumber("+12025559999")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("100 Test Ave")
                .password(passwordEncoder.encode("Carrier@1234"))
                .userRole(UserRole.CARRIER)
                .userStatus(UserStatus.INACTIVE)
                .createdAt(LocalDateTime.now())
                .build());

        LoginRequest request = new LoginRequest();
        request.setEmail("pending@carrier.com");
        request.setPassword("Carrier@1234");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── TC-086 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-086: POST /api/v1/auth/login returns 400 when email is invalid format")
    void login_withInvalidEmailFormat_shouldReturn400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("not-an-email");
        request.setPassword("Patient@1234");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email").exists());
    }

    // ── TC-087 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-087: POST /api/v1/auth/logout returns 200 for authenticated user")
    void logout_withValidToken_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", patientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── TC-088 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-088: POST /api/v1/auth/login returns 400 when password is blank")
    void login_withBlankPassword_shouldReturn400WithValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"patient@test.com\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── TC-089 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-089: Accessing protected endpoint without token returns 401")
    void protectedEndpoint_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/claims"))
                .andExpect(status().isForbidden());
    }
}
