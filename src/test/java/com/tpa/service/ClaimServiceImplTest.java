package com.tpa.service;

import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.claim.ClaimDecisionResponse;
import com.tpa.dto.response.claim.ClaimResponse;
import com.tpa.entity.*;
import com.tpa.enums.ClaimStatus;
import com.tpa.exception.BadRequestException;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.helper.AuditForensicService;
import com.tpa.helper.ClaimStateMachine;
import com.tpa.helper.PdfExportService;
import com.tpa.kafka.producer.ProducerService;
import com.tpa.mapper.ClaimMapper;
import com.tpa.repository.*;
import com.tpa.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TC-033 to TC-044: ClaimService Unit Tests (Mockito-based)
 * Tests claim creation, retrieval, pagination, role-based access control,
 * state machine integration, bulk approval, and claim deletion guards.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClaimService - Business Logic Unit Tests")
class ClaimServiceImplTest {

    @Mock private ClaimRepository claimRepository;
    @Mock private UserRepository userRepository;
    @Mock private ClaimDocumentRepository claimDocumentRepository;
    @Mock private CarrierRepository carrierRepository;
    @Mock private ClaimAuditRepository claimAuditRepository;
    @Mock private ClaimQueryRepository claimQueryRepository;
    @Mock private ClaimMapper claimMapper;
    @Mock private ClaimStateMachine claimStateMachine;
    @Mock private ProducerService producerService;
    @Mock private AuditLogService auditLogService;
    @Mock private PdfExportService pdfExportService;
    @Mock private CarrierService carrierService;
    @Mock private PaymentService paymentService;
    @Mock private AuditForensicService auditForensicService;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private com.tpa.service.impl.ClaimServiceImpl claimService;

    private User patient;
    private Claim claim;
    private ClaimResponse claimResponse;

    @BeforeEach
    void setUp() {
        patient = TestDataFactory.buildPatientUser();
        patient.setId(1L);
        claim = TestDataFactory.buildSubmittedClaim(patient);
        claim.setId(10L);

        claimResponse = new ClaimResponse();
        claimResponse.setUserEmail("patient@test.com");
    }

    // ── TC-033 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-033: createClaim maps request to entity and saves with SUBMITTED status")
    void createClaim_shouldSaveWithSubmittedStatus() {
        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(carrierRepository.findByCompanyNameIgnoreCase(anyString())).thenReturn(Optional.of(new Carrier()));
        when(claimMapper.toClaim(any())).thenReturn(claim);
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);
        when(claimMapper.toClaimResponse(any())).thenReturn(claimResponse);

        ClaimRequest request = TestDataFactory.buildValidClaimRequest();
        ClaimResponse result = claimService.createClaim(request, "patient@test.com");

        assertThat(result).isNotNull();
        verify(claimRepository).save(argThat(c -> c.getClaimStatus() == ClaimStatus.SUBMITTED));
        verify(auditLogService).logAction(anyLong(), eq("CLAIM_CREATED"), isNull(), eq(ClaimStatus.SUBMITTED));
    }

    // ── TC-034 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-034: createClaim throws NoResourceFoundException when carrier not found")
    void createClaim_withUnknownCarrier_shouldThrowNotFound() {
        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(carrierRepository.findByCompanyNameIgnoreCase("Unknown Corp"))
                .thenReturn(Optional.empty());

        ClaimRequest request = TestDataFactory.buildValidClaimRequest();
        request.setCarrierName("Unknown Corp");

        assertThatThrownBy(() -> claimService.createClaim(request, "patient@test.com"))
                .isInstanceOf(NoResourceFoundException.class);
    }

    // ── TC-035 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-035: getClaim throws RuntimeException when claim does not exist")
    void getClaim_withNonExistentId_shouldThrowException() {
        when(claimRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> claimService.getClaim(999L, "patient@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Claim not found");
    }

    // ── TC-036 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-036: getClaim throws AccessDeniedException when patient accesses another user's claim")
    void getClaim_byPatientForOtherUserClaim_shouldThrowAccessDenied() {
        ClaimResponse foreignClaim = new ClaimResponse();
        foreignClaim.setUserEmail("other@test.com");

        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenAnswer(inv ->
                List.of(new SimpleGrantedAuthority("ROLE_PATIENT")));
        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);

        when(claimRepository.findById(10L)).thenReturn(Optional.of(claim));
        when(claimMapper.toClaimResponse(claim)).thenReturn(foreignClaim);

        assertThatThrownBy(() -> claimService.getClaim(10L, "patient@test.com"))
                .isInstanceOf(AccessDeniedException.class);

        SecurityContextHolder.clearContext();
    }

    // ── TC-037 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-037: getAllClaims returns all claims for ADMIN role (no user filtering)")
    void getAllClaims_forAdmin_shouldReturnAllClaims() {
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenAnswer(inv ->
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);

        Page<Claim> page = new PageImpl<>(List.of(claim));
        when(claimRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        when(claimMapper.toClaimResponse(any())).thenReturn(claimResponse);

        Page<ClaimResponse> result = claimService.getAllClaims(PageRequest.of(0, 10), "admin@test.com");

        assertThat(result.getTotalElements()).isEqualTo(1);
        SecurityContextHolder.clearContext();
    }

    // ── TC-038 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-038: processClaimDecision skips processing for already SETTLED claim")
    void processClaimDecision_forSettledClaim_shouldReturnEarly() {
        claim.setClaimStatus(ClaimStatus.SETTLED);
        when(claimRepository.findById(10L)).thenReturn(Optional.of(claim));

        ClaimDecisionResponse decision = new ClaimDecisionResponse();
        decision.setClaimStatus(ClaimStatus.ADMIN_APPROVED);

        claimService.processClaimDecision(10L, decision);

        verify(claimRepository, never()).save(claim);
    }

    // ── TC-039 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-039: deleteClaim throws BadRequestException when claim is not in SUBMITTED or UNDER_REVIEW")
    void deleteClaim_withApprovedClaim_shouldThrowBadRequest() {
        claim.setClaimStatus(ClaimStatus.ADMIN_APPROVED);
        patient.setId(1L);

        when(claimRepository.findById(10L)).thenReturn(Optional.of(claim));
        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));

        assertThatThrownBy(() -> claimService.deleteClaim(10L, "patient@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot delete claim");
    }

    // ── TC-040 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-040: processBulkApproval returns correct success/failure counts")
    void processBulkApproval_mixedResults_shouldReturnCorrectCounts() {
        Claim approvedClaim = TestDataFactory.buildAdminApprovedClaim(patient, new Carrier());
        approvedClaim.setId(10L);

        // claimId 10 → found, saved, payout succeeds → success
        when(claimRepository.findById(10L)).thenReturn(Optional.of(approvedClaim));
        when(claimRepository.save(any())).thenReturn(approvedClaim);
        // claimId 99 → not found → RuntimeException → fail
        when(claimRepository.findById(99L)).thenReturn(Optional.empty());

        var result = claimService.processBulkApproval(List.of(10L, 99L), "admin@test.com");

        assertThat(result.getTotalProcessed()).isEqualTo(2);
        assertThat(result.getSuccess()).isEqualTo(1);
        assertThat(result.getFailed()).isEqualTo(1);
    }

    // ── TC-041 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-041: broadcastStatusUpdate invokes SimpMessagingTemplate with correct topic")
    void broadcastStatusUpdate_shouldCallMessagingTemplateWithCorrectTopic() {
        claimService.broadcastStatusUpdate(10L, "APPROVED", "Claim approved");

        verify(messagingTemplate).convertAndSend(
                eq("/topic/claims/10"),
                (Object) any(java.util.Map.class)
        );
    }

    // ── TC-042 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-042: sendUserNotification sends to correct user queue")
    void sendUserNotification_shouldSendToCorrectUserDestination() {
        claimService.sendUserNotification("patient@test.com", "Title", "Message");

        verify(messagingTemplate).convertAndSendToUser(
                eq("patient@test.com"),
                eq("/queue/notifications"),
                argThat(payload -> payload instanceof java.util.Map)
        );
    }

    // ── TC-043 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-043: createClaim generates TEMP policy number when policyNumber is blank")
    void createClaim_withBlankPolicyNumber_shouldGenerateTempPolicyNumber() {
        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(claimMapper.toClaim(any())).thenReturn(claim);
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));
        when(claimMapper.toClaimResponse(any())).thenReturn(claimResponse);

        ClaimRequest request = TestDataFactory.buildValidClaimRequest();
        request.setPolicyNumber(null);
        request.setCarrierName(null); // No carrier lookup

        claimService.createClaim(request, "patient@test.com");

        verify(claimRepository).save(argThat(c ->
                c.getPolicyNumber() != null && c.getPolicyNumber().startsWith("TEMP-")
        ));
    }

    // ── TC-044 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-044: getClaimQueries returns queries for valid claim in ascending order")
    void getClaimQueries_shouldReturnQueriesInTimestampAscOrder() {
        when(claimRepository.findById(10L)).thenReturn(Optional.of(claim));
        when(claimMapper.toClaimResponse(any())).thenReturn(claimResponse);
        claimResponse.setUserEmail("patient@test.com");

        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenAnswer(inv ->
                List.of(new SimpleGrantedAuthority("ROLE_PATIENT")));
        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);

        ClaimQuery q1 = ClaimQuery.builder()
                .claim(claim).senderUsername("patient@test.com").message("Query 1").build();
        ClaimQuery q2 = ClaimQuery.builder()
                .claim(claim).senderUsername("patient@test.com").message("Query 2").build();
        when(claimQueryRepository.findByClaimIdOrderByTimestampAsc(10L)).thenReturn(List.of(q1, q2));

        var queries = claimService.getClaimQueries(10L, "patient@test.com");
        assertThat(queries).hasSize(2);

        SecurityContextHolder.clearContext();
    }
}
