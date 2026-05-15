package com.tpa.controller;

import com.tpa.dto.response.claim.ClaimResponse;
import com.tpa.helper.AdminInitializer;
import com.tpa.helper.EnterpriseDataSeeder;
import com.tpa.service.ClaimService;
import com.tpa.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")

class SecurityAndUploadTest {

    @Autowired 
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    @MockBean
    private ClaimService claimService;

    @MockBean
    private AdminInitializer adminInitializer;

    @MockBean
    private EnterpriseDataSeeder enterpriseDemoDataSeeder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Should reject unauthorized access to claim timeline")
    void getTimeline_Unauthenticated_Returns401Or403() throws Exception {
        mockMvc.perform(get("/api/v1/claims/1/timeline"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 401 || status == 403;
                });
    }

    @Test
    @WithMockUser(username = "customer@tpa.com", roles = "CUSTOMER")
    @DisplayName("Should allow customer to access their own timeline")
    void getTimeline_Authorized_Returns200() throws Exception {
        ClaimResponse mockClaim = ClaimResponse.builder()
                .id(1L)
                .userEmail("customer@tpa.com")
                .build();
        org.mockito.Mockito.when(claimService.getClaim(org.mockito.ArgumentMatchers.anyLong())).thenReturn(mockClaim);
        org.mockito.Mockito.when(claimService.getClaimAudits(org.mockito.ArgumentMatchers.anyLong())).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/claims/1/timeline"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print()).andExpect(status().isOk());
    }
}
