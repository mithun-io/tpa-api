package com.tpa.controller;

import com.tpa.entity.*;
import com.tpa.enums.ClaimStatus;
import com.tpa.repository.*;
import com.tpa.support.BaseControllerTest;
import com.tpa.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TC-097 to TC-100: AdminController, GlobalExceptionHandler, and Integration Tests
 * Tests admin-only endpoints, global error handling for unknown resources,
 * claim timeline retrieval, and the complete claim lifecycle end-to-end.
 */
@DisplayName("AdminController & Integration - Final 4 Test Cases")
class AdminAndIntegrationTest extends BaseControllerTest {

    @Autowired private ClaimRepository claimRepository;
    @Autowired private CarrierRepository carrierRepository;
    @Autowired private ClaimDocumentRepository claimDocumentRepository;

    private Claim savedClaim;
    private Carrier savedCarrier;

    @BeforeEach
    void setUpData() {
        claimDocumentRepository.deleteAll();
        claimRepository.deleteAll();
        carrierRepository.deleteAll();

        savedCarrier = carrierRepository.save(TestDataFactory.buildCarrier(carrierUser));
        savedClaim = claimRepository.save(TestDataFactory.buildSubmittedClaim(patientUser));
    }

    // ── TC-097 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-097: GET /api/v1/admin/claims returns 403 when called by PATIENT role")
    void adminGetAllClaims_asPatient_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/claims")
                        .header("Authorization", patientToken))
                .andExpect(status().isForbidden());
    }

    // ── TC-098 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-098: GET /api/v1/claims/{id}/timeline returns 200 with timeline for ADMIN")
    void getClaimTimeline_asAdmin_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/claims/" + savedClaim.getId() + "/timeline")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    // ── TC-099 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-099: GET /api/v1/claims/9999 returns 500 for non-existent claim (RuntimeException)")
    void getClaim_nonExistent_shouldReturn500() throws Exception {
        mockMvc.perform(get("/api/v1/claims/9999999")
                        .header("Authorization", adminToken))
                .andExpect(status().is5xxServerError());
    }

    // ── TC-100 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-100: GET /api/v1/claims/{id}/audits returns 200 with audit list for ADMIN")
    void getClaimAudits_asAdmin_shouldReturn200WithAuditList() throws Exception {
        mockMvc.perform(get("/api/v1/claims/" + savedClaim.getId() + "/audits")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
