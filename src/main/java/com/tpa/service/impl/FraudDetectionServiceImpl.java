package com.tpa.service.impl;

import com.tpa.dto.response.claim.FraudClaimResponse;
import com.tpa.dto.response.auth.FraudDashboardResponse;
import com.tpa.entity.Carrier;
import com.tpa.entity.Claim;
import com.tpa.entity.User;
import com.tpa.enums.RiskLevel;
import com.tpa.mapper.FraudClaimMapper;
import com.tpa.repository.CarrierRepository;
import com.tpa.repository.ClaimDocumentRepository;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.UserRepository;
import com.tpa.service.FraudDetectionService;
import com.tpa.service.MedicalValidationService;
import com.tpa.service.StorageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private final ClaimRepository claimRepository;
    private final CarrierRepository carrierRepository;
    private final UserRepository userRepository;
    private final ClaimDocumentRepository claimDocumentRepository;

    private final MedicalValidationService medicalValidationService;

    private final StorageProvider storageProvider;

    private final FraudClaimMapper fraudClaimMapper;

    private static final Set<String> BLACKLISTED_HOSPITALS = Set.of("Fake Hospital City", "Fraudulent Medical Center", "Scam Healthcare");
    private static final Set<String> SUSPICIOUS_PRODUCERS = Set.of("Adobe Photoshop", "Adobe Illustrator", "Canva", "CorelDRAW", "GIMP");

    @Override
    public void calculateAndSaveHealthAndRisk(Claim claim) {
        if (claim == null) return;
        
        if ("LOW".equals(claim.getRiskLevel()) && claim.getHealthScore() != null && claim.getHealthScore() == 100) {
            return;
        }

        List<String> reasons = new ArrayList<>();
        double riskScore = 0.0;

        if (claim.getAmount() != null) {
            if (claim.getAmount() > 50000) {
                riskScore += 30;
                reasons.add("High claim amount (> $50,000)");
            } else if (claim.getAmount() > 10000) {
                riskScore += 10;
                reasons.add("Elevated claim amount (> $10,000)");
            }
            if (claim.getTotalBillAmount() != null && !claim.getAmount().equals(claim.getTotalBillAmount())) {
                riskScore += 20;
                reasons.add("Claimed amount does not match total bill amount");
            }
        }

        if (claim.getPatientName() == null || claim.getPolicyNumber() == null) {
            riskScore += 15;
            reasons.add("Critical data missing from documents");
        }

        if (claim.getAdmissionDate() != null && claim.getDischargeDate() != null) {
            if (claim.getDischargeDate().isBefore(claim.getAdmissionDate())) {
                riskScore += 40;
                reasons.add("Discharge date is before admission date");
            } else if (claim.getAdmissionDate().equals(claim.getDischargeDate())) {
                riskScore += 15;
                reasons.add("Zero-day hospital stay pattern detected");
            }
        }

        if (claim.getRiskScore() != null && claim.getRiskScore() > 0) {
            riskScore += claim.getRiskScore();

            if (claim.getRiskFlags() != null && !claim.getRiskFlags().isBlank()) {
                reasons.add("AI Document flag: " + claim.getRiskFlags());
            }
        }

        // 1. Hospital Blacklist Check
        if (claim.getHospitalName() != null && BLACKLISTED_HOSPITALS.stream().anyMatch(bh -> claim.getHospitalName().toLowerCase().contains(bh.toLowerCase()))) {
            riskScore += 50;
            reasons.add("Hospital is in the global fraud blacklist");
        }

        // 2. Duplicate Bill Number Check (Industry standard for fraud prevention)
        if (claim.getBillNumber() != null && !claim.getBillNumber().isBlank()) {
            boolean duplicateBill = claimRepository.existsByBillNumberAndIdNot(claim.getBillNumber(), claim.getId());

            if (duplicateBill) {
                riskScore += 60;
                reasons.add("Duplicate bill number detected across multiple claims");
            }
        }

        // 3. PDF Metadata Forensic Analysis
        List<com.tpa.entity.ClaimDocument> docs = claimDocumentRepository.findByClaim(claim);
        for (com.tpa.entity.ClaimDocument doc : docs) {
            if ("PDF".equalsIgnoreCase(doc.getFileType())) {
                String forensicIssues = analyzePdfMetadata(doc.getFilePath());

                if (forensicIssues != null) {
                    riskScore += 40;
                    reasons.add("Forensic Alert: " + forensicIssues);
                }
            }
        }

        // 4. Medical Consistency & High-Risk Diagnosis Check
        if (claim.getIcdCode() != null) {
            List<String> medicalIssues = medicalValidationService.validateIcdCode(claim.getIcdCode(), claim.getDiagnosis());

            if (!medicalIssues.isEmpty()) {
                riskScore += 30;
                reasons.addAll(medicalIssues);
            }

            if (medicalValidationService.isHighRiskDiagnosis(claim.getIcdCode())) {
                riskScore += 20;
                reasons.add("High-risk medical diagnosis detected (requires specialized review)");
            }
        }

        riskScore = Math.min(riskScore, 100.0);
        
        String riskLevel = "LOW";

        if (riskScore >= 70) {
            riskLevel = "HIGH";
        } else if (riskScore >= 30) {
            riskLevel = "MEDIUM";
        }
        
        double healthScore = 100 - riskScore;

        claim.setRiskScore(riskScore);
        claim.setRiskLevel(RiskLevel.valueOf(riskLevel));
        claim.setRiskFlags(String.join(", ", reasons));
        claim.setHealthScore(healthScore);
        
        claimRepository.save(claim);
        log.info("Calculated HealthScore: {} for Claim ID: {}", healthScore, claim.getId());
    }

    private String analyzePdfMetadata(String filePath) {
        try (InputStream is = storageProvider.loadFileAsResource(filePath).getInputStream();
             PDDocument pdDocument = Loader.loadPDF(is.readAllBytes())) {
            
            PDDocumentInformation pdDocumentInformation = pdDocument.getDocumentInformation();

            String producer = pdDocumentInformation.getProducer();
            String creator = pdDocumentInformation.getCreator();

            if (producer != null && SUSPICIOUS_PRODUCERS.stream().anyMatch(p -> producer.contains(p))) {
                return "Document was produced using editing software: " + producer;
            }
            if (creator != null && SUSPICIOUS_PRODUCERS.stream().anyMatch(p -> creator.contains(p))) {
                return "Document was created using editing software: " + creator;
            }
            
            return null;
        } catch (Exception e) {
            log.warn("Failed to perform forensic analysis on PDF: {}. Skipping.", filePath);
            return null;
        }
    }

    @Override
    public FraudDashboardResponse getAdminFraudDashboard() {
        List<Claim> allClaims = claimRepository.findAll();

        if (allClaims == null) {
            allClaims = new ArrayList<>();
        }

        return generateDashboardResponse(allClaims);
    }

    @Override
    public FraudDashboardResponse getCarrierFraudDashboard(String carrierEmail) {
        User user = userRepository.findByEmail(carrierEmail).orElseThrow(() -> new RuntimeException("User not found"));
                
        Carrier carrier = carrierRepository.findByUser_Id(user.getId()).orElseThrow(() -> new RuntimeException("Carrier not found for user ID: " + user.getId()));
        
        List<Claim> carrierClaims = claimRepository.findByCarrier_Id(carrier.getId());

        if (carrierClaims == null) {
            carrierClaims = new ArrayList<>();
        }
        return generateDashboardResponse(carrierClaims);
    }

    @Override
    public void markClaimAsSafe(Long claimId) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setRiskScore(0.0);
        claim.setRiskLevel(RiskLevel.LOW);
        claim.setHealthScore(100d);
        claim.setRiskFlags("Marked safe by Admin");

        claimRepository.save(claim);
        log.info("Claim {} marked as safe", claimId);
    }

    private FraudDashboardResponse generateDashboardResponse(List<Claim> claims) {
        int total = claims.size();
        int highRisk = 0;
        int mediumRisk = 0;
        int lowRisk = 0;
        
        List<FraudClaimResponse> fraudClaimResponses = new ArrayList<>();

        for (Claim c : claims) {
            if (c.getHealthScore() == null) {
                calculateAndSaveHealthAndRisk(c);
            }
            
            String level = c.getRiskLevel() != null ? c.getRiskLevel().name() : "LOW";
            if ("HIGH".equals(level)) highRisk++;
            else if ("MEDIUM".equals(level)) mediumRisk++;
            else lowRisk++;

            fraudClaimResponses.add(fraudClaimMapper.toFraudClaimDto(c));
        }

        FraudDashboardResponse.DashboardStats dashboardStats = FraudDashboardResponse.DashboardStats.builder()
                .totalClaims(total)
                .highRisk(highRisk)
                .mediumRisk(mediumRisk)
                .lowRisk(lowRisk)
                .flagged(highRisk + mediumRisk)
                .build();

        return FraudDashboardResponse.builder()
                .dashboardStats(dashboardStats)
                .claims(fraudClaimResponses)
                .build();
    }
}
