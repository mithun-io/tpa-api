package com.tpa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TC-090 to TC-096: ClaimController Integration Tests
 * Tests RBAC enforcement, pagination, carrier approval guard,
 * bulk approval, claim queries, PDF export content type,
 * and patient-only claim creation.
 */
@DisplayName("ClaimController - REST API Integration Tests")
class ClaimControllerTest extends BaseControllerTest {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private ClaimRepository claimRepository;
    @Autowired private CarrierRepository carrierRepository;
    @Autowired private ClaimDocumentRepository claimDocumentRepository;

    private Claim savedClaim;
    private Carrier savedCarrier;

    @BeforeEach
    void setUpClaims() {
        claimDocumentRepository.deleteAll();
        claimRepository.deleteAll();
        carrierRepository.deleteAll();

        // Carrier is created using the carrierUser saved in BaseControllerTest
        savedCarrier = carrierRepository.save(TestDataFactory.buildCarrier(carrierUser));

        Claim claim = TestDataFactory.buildSubmittedClaim(patientUser);
        claim.setCarrier(savedCarrier);
        savedClaim = claimRepository.save(claim);
    }


    // ── TC-090 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-090: GET /api/v1/claims returns 200 with paginated data for authenticated ADMIN")
    void getAllClaims_asAdmin_shouldReturn200WithPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/v1/claims")
                        .header("Authorization", adminToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    // ── TC-091 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-091: GET /api/v1/claims/{id} returns 200 with claim details for ADMIN")
    void getClaim_asAdmin_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/claims/" + savedClaim.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    // ── TC-092 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-092: POST /api/v1/claims returns 403 when called by non-PATIENT role")
    void createClaim_asAdmin_shouldReturn403() throws Exception {
        String requestBody = objectMapper.writeValueAsString(TestDataFactory.buildValidClaimRequest());

        mockMvc.perform(post("/api/v1/claims")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    // ── TC-093 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-093: DELETE /api/v1/claims/{id} returns 403 when called by CARRIER")
    void deleteClaim_asCarrier_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/v1/claims/" + savedClaim.getId())
                        .header("Authorization", carrierToken))
                .andExpect(status().isForbidden());
    }

    // ── TC-094 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-094: PUT /api/v1/claims/{id}/carrier-approve returns 403 when called by non-CARRIER")
    void carrierApproveClaim_asPatient_shouldReturn403() throws Exception {
        mockMvc.perform(put("/api/v1/claims/" + savedClaim.getId() + "/carrier-approve")
                        .header("Authorization", patientToken))
                .andExpect(status().isForbidden());
    }

    // ── TC-095 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-095: POST /api/v1/claims/bulk-approve returns 403 when called by PATIENT")
    void bulkApprove_asPatient_shouldReturn403() throws Exception {
        mockMvc.perform(post("/api/v1/claims/bulk-approve")
                        .header("Authorization", patientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[" + savedClaim.getId() + "]"))
                .andExpect(status().isForbidden());
    }

    // ── TC-096 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-096: GET /api/v1/claims/{id}/export returns PDF content-type for ADMIN")
    void exportClaimReport_asAdmin_shouldReturnPdfContentType() throws Exception {
        mockMvc.perform(get("/api/v1/claims/" + savedClaim.getId() + "/export")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=claim-report-" + savedClaim.getId() + ".pdf"));
    }
}
