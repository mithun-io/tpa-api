package com.tpa.service.impl;

import com.tpa.dto.response.auth.FraudDashboardResponse;
import com.tpa.entity.Claim;
import com.tpa.enums.RiskLevel;
import com.tpa.repository.CarrierRepository;
import com.tpa.repository.ClaimDocumentRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.UserRepository;
import com.tpa.service.MedicalValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private CarrierRepository carrierRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClaimDocumentRepository claimDocumentRepository;
    @Mock
    private MedicalValidationService medicalValidationService;
    @Mock
    private StorageProvider storageProvider;
    @Spy
    private com.tpa.mapper.FraudClaimMapper fraudClaimMapper =
            new com.tpa.mapper.FraudClaimMapperImpl();

    @InjectMocks
    private FraudDetectionServiceImpl fraudDetectionService;

    private Claim claim;

    @BeforeEach
    void setUp() {
        claim = new Claim();
        claim.setId(1L);
        claim.setPolicyNumber("POL-123");
        claim.setPatientName("John Doe");
        lenient().when(claimDocumentRepository.findByClaim(any())).thenReturn(List.of());
    }

    @Test
    void calculateAndSaveHealthAndRisk_HighAmount_ShouldIncreaseRisk() {
        claim.setAmount(60000.0);
        claim.setTotalBillAmount(60000.0);
        
        fraudDetectionService.calculateAndSaveHealthAndRisk(claim);
        
        assertThat(claim.getRiskScore()).isGreaterThanOrEqualTo(30.0);
        assertThat(claim.getRiskFlags()).contains("High claim amount");
    }

    @Test
    void calculateAndSaveHealthAndRisk_MismatchAmount_ShouldIncreaseRisk() {
        claim.setAmount(1000.0);
        claim.setTotalBillAmount(2000.0);
        
        fraudDetectionService.calculateAndSaveHealthAndRisk(claim);
        
        assertThat(claim.getRiskScore()).isGreaterThanOrEqualTo(20.0);
        assertThat(claim.getRiskFlags()).contains("Claimed amount does not match total bill amount");
    }

    @Test
    void calculateAndSaveHealthAndRisk_MissingCriticalData_ShouldIncreaseRisk() {
        claim.setPatientName(null);
        claim.setAmount(5000.0);
        claim.setTotalBillAmount(5000.0);
        
        fraudDetectionService.calculateAndSaveHealthAndRisk(claim);
        
        assertThat(claim.getRiskScore()).isGreaterThanOrEqualTo(15.0);
        assertThat(claim.getRiskFlags()).contains("Critical data missing");
    }

    @Test
    void calculateAndSaveHealthAndRisk_DischargeBeforeAdmission_ShouldFlagHighRisk() {
        claim.setAdmissionDate(LocalDate.of(2026, 1, 5));
        claim.setDischargeDate(LocalDate.of(2026, 1, 1)); // Invalid
        
        fraudDetectionService.calculateAndSaveHealthAndRisk(claim);
        
        assertThat(claim.getRiskScore()).isGreaterThanOrEqualTo(40.0);
        assertThat(claim.getRiskFlags()).contains("Discharge date is before admission date");
    }

    @Test
    void calculateAndSaveHealthAndRisk_ZeroDayStay_ShouldFlagRisk() {
        claim.setAdmissionDate(LocalDate.of(2026, 1, 1));
        claim.setDischargeDate(LocalDate.of(2026, 1, 1)); // Same day
        
        fraudDetectionService.calculateAndSaveHealthAndRisk(claim);
        
        assertThat(claim.getRiskScore()).isGreaterThanOrEqualTo(15.0);
        assertThat(claim.getRiskFlags()).contains("Zero-day hospital stay");
    }

    @Test
    void getAdminFraudDashboard_ShouldReturnStats() {
        claim.setRiskLevel(RiskLevel.HIGH);
        claim.setHealthScore(50);
        when(claimRepository.findAll()).thenReturn(List.of(claim));
        
        FraudDashboardResponse response = fraudDetectionService.getAdminFraudDashboard();
        
        assertThat(response.getStats().getTotalClaims()).isEqualTo(1);
        assertThat(response.getStats().getHighRisk()).isEqualTo(1);
        assertThat(response.getClaims().get(0).getRiskLevel()).isEqualTo(RiskLevel.HIGH);
    }
    
    @Test
    void markClaimAsSafe_ShouldResetRiskScore() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        
        fraudDetectionService.markClaimAsSafe(1L);
        
        assertThat(claim.getRiskScore()).isEqualTo(0.0);
        assertThat(claim.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(claim.getHealthScore()).isEqualTo(100);
        verify(claimRepository).save(claim);
    }
}
