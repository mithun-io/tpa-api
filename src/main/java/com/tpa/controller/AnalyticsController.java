package com.tpa.controller;

import com.tpa.entity.Claim;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.RiskLevel;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.PaymentLedgerRepository;
import com.tpa.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics dashboard API providing:
 * - Fraud trend analysis
 * - SLA performance metrics
 * - Claim leakage analysis
 * - Hospital analytics
 * - Predictive volume forecasting
 * - Payment settlement analysis
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('FMG_ADMIN', 'CARRIER_USER')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ClaimRepository claimRepository;
    private final PaymentLedgerRepository paymentLedgerRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(analyticsService.getDashboardAnalytics());
    }

    @GetMapping("/fraud/trends")
    public ResponseEntity<Map<String, Object>> getFraudTrends() {
        List<Claim> allClaims = claimRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<>();

        long highRisk = allClaims.stream().filter(c -> c.getRiskLevel() == RiskLevel.HIGH).count();
        long medRisk  = allClaims.stream().filter(c -> c.getRiskLevel() == RiskLevel.MEDIUM).count();
        long lowRisk  = allClaims.stream()
                .filter(c -> c.getRiskLevel() == RiskLevel.LOW || c.getRiskLevel() == null).count();

        result.put("riskDistribution", Map.of("HIGH", highRisk, "MEDIUM", medRisk, "LOW", lowRisk));
        result.put("fraudRate", allClaims.isEmpty() ? 0.0 :
                Math.round((double)(highRisk + medRisk) / allClaims.size() * 100 * 100.0) / 100.0);

        Map<String, Long> hospitalRisk = allClaims.stream()
                .filter(c -> c.getRiskLevel() == RiskLevel.HIGH && c.getHospitalName() != null)
                .collect(Collectors.groupingBy(Claim::getHospitalName, Collectors.counting()));
        result.put("topRiskHospitals", hospitalRisk.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, LinkedHashMap::new)));

        double avgFraudScore = allClaims.stream()
                .filter(c -> c.getRiskScore() != null)
                .mapToDouble(Claim::getRiskScore).average().orElse(0.0);
        result.put("averageFraudScore", Math.round(avgFraudScore * 100.0) / 100.0);
        result.put("generatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sla/performance")
    public ResponseEntity<Map<String, Object>> getSlaPerformance() {
        List<Claim> allClaims = claimRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> result = new LinkedHashMap<>();

        long total = allClaims.size();
        long breached = allClaims.stream()
                .filter(c -> c.getSlaDeadline() != null && c.getSlaDeadline().isBefore(now))
                .filter(c -> !List.of(ClaimStatus.SETTLED, ClaimStatus.REJECTED).contains(c.getStatus()))
                .count();
        long escalated = allClaims.stream().filter(c -> Boolean.TRUE.equals(c.getEscalated())).count();

        result.put("totalClaims", total);
        result.put("withinSla", total - breached);
        result.put("slaBreached", breached);
        result.put("escalated", escalated);
        result.put("slaComplianceRate", total == 0 ? 100.0 :
                Math.round((double)(total - breached) / total * 100 * 100.0) / 100.0);

        OptionalDouble avgHours = allClaims.stream()
                .filter(c -> c.getProcessedDate() != null && c.getCreatedDate() != null)
                .mapToLong(c -> java.time.Duration.between(c.getCreatedDate(), c.getProcessedDate()).toHours())
                .average();
        result.put("avgProcessingHours", Math.round(avgHours.orElse(0.0) * 10.0) / 10.0);
        result.put("generatedAt", now.toString());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/leakage")
    public ResponseEntity<Map<String, Object>> getClaimLeakage() {
        List<Claim> allClaims = claimRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<>();

        double totalClaimed   = allClaims.stream().filter(c -> c.getAmount() != null)
                .mapToDouble(Claim::getAmount).sum();
        double approvedPayout = Optional.ofNullable(claimRepository.sumApprovedClaimAmount()).orElse(0.0);
        long mismatchCount    = allClaims.stream()
                .filter(c -> c.getAmount() != null && c.getTotalBillAmount() != null)
                .filter(c -> !c.getAmount().equals(c.getTotalBillAmount())).count();

        result.put("totalClaimedAmount", totalClaimed);
        result.put("totalApprovedPayout", approvedPayout);
        result.put("leakageAmount", totalClaimed - approvedPayout);
        result.put("leakageRate", totalClaimed == 0 ? 0.0 :
                Math.round((totalClaimed - approvedPayout) / totalClaimed * 100 * 100.0) / 100.0);
        result.put("amountMismatchCount", mismatchCount);
        result.put("generatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/hospitals")
    public ResponseEntity<Map<String, Object>> getHospitalAnalytics() {
        List<Claim> allClaims = claimRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<>();

        Map<String, Long> claimsPerHospital = allClaims.stream()
                .filter(c -> c.getHospitalName() != null)
                .collect(Collectors.groupingBy(Claim::getHospitalName, Collectors.counting()));

        Map<String, Double> avgAmountPerHospital = allClaims.stream()
                .filter(c -> c.getHospitalName() != null && c.getAmount() != null)
                .collect(Collectors.groupingBy(Claim::getHospitalName, Collectors.averagingDouble(Claim::getAmount)));

        result.put("topHospitalsByVolume", claimsPerHospital.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, LinkedHashMap::new)));
        result.put("topHospitalsByAmount", avgAmountPerHospital.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, LinkedHashMap::new)));
        result.put("totalUniqueHospitals", claimsPerHospital.size());
        result.put("generatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/forecast")
    public ResponseEntity<Map<String, Object>> getVolumeForecast() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> dailyCounts = claimRepository.countClaimsPerDay(thirtyDaysAgo);
        Map<String, Object> result = new LinkedHashMap<>();

        long totalLast30 = dailyCounts.stream().mapToLong(r -> ((Number) r[1]).longValue()).sum();
        double dailyAvg  = totalLast30 / 30.0;

        result.put("dailyAverageLast30Days", Math.round(dailyAvg * 10.0) / 10.0);
        result.put("forecastNext7Days", Math.round(dailyAvg * 7));
        result.put("forecastNext30Days", Math.round(dailyAvg * 30));
        result.put("historicalData", dailyCounts.stream().map(r -> Map.of(
                "date", r[0].toString(), "count", ((Number) r[1]).longValue()
        )).collect(Collectors.toList()));
        result.put("generatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/payments/summary")
    public ResponseEntity<Map<String, Object>> getPaymentSummary() {
        Double totalSettled  = paymentLedgerRepository.sumVerifiedPayments();
        long successCount    = paymentLedgerRepository.findByEventTypeOrderByCreatedAtDesc("PAYMENT_VERIFIED").size();
        long failedCount     = paymentLedgerRepository.findByEventTypeOrderByCreatedAtDesc("PAYMENT_FAILED").size();
        long total           = successCount + failedCount;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalSettledAmount", totalSettled != null ? totalSettled : 0.0);
        result.put("totalSuccessfulSettlements", successCount);
        result.put("totalFailedPayments", failedCount);
        result.put("successRate", total == 0 ? 100.0 : Math.round((double)successCount / total * 100 * 100.0) / 100.0);
        result.put("generatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Loss Ratio: Total Claims Paid / Total Premiums (simulated).
     * Loss Ratio > 100% means the carrier is paying out more than it collects.
     */
    @GetMapping("/loss-ratio")
    public ResponseEntity<Map<String, Object>> getLossRatio() {
        List<Claim> allClaims = claimRepository.findAll();
        Double totalPaid = paymentLedgerRepository.sumVerifiedPayments();
        if (totalPaid == null) totalPaid = 0.0;

        // Simulate: assume premium = 30% of claim amount on average
        double estimatedPremiumPool = allClaims.stream()
                .filter(c -> c.getAmount() != null)
                .mapToDouble(Claim::getAmount)
                .sum() * 0.3;

        double lossRatio = estimatedPremiumPool == 0 ? 0 :
                Math.round(totalPaid / estimatedPremiumPool * 100 * 100.0) / 100.0;

        long settledClaims = allClaims.stream()
                .filter(c -> ClaimStatus.SETTLED.equals(c.getStatus())).count();
        long rejectedClaims = allClaims.stream()
                .filter(c -> ClaimStatus.REJECTED.equals(c.getStatus())).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalClaimsPaid", totalPaid);
        result.put("estimatedPremiumPool", Math.round(estimatedPremiumPool * 100.0) / 100.0);
        result.put("lossRatioPercent", lossRatio);
        result.put("lossRatioStatus", lossRatio > 100 ? "LOSS" : lossRatio > 75 ? "HIGH" : lossRatio > 50 ? "MODERATE" : "HEALTHY");
        result.put("settledClaims", settledClaims);
        result.put("rejectedClaims", rejectedClaims);
        result.put("totalClaims", allClaims.size());
        result.put("generatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Carrier-specific analytics: filters data by carrier name.
     */
    @GetMapping("/carrier/{carrierName}/summary")
    public ResponseEntity<Map<String, Object>> getCarrierSummary(@PathVariable String carrierName) {
        List<Claim> carrierClaims = claimRepository.findAll().stream()
                .filter(c -> carrierName.equalsIgnoreCase(c.getCarrierName()))
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("carrier", carrierName);
        result.put("totalClaims", carrierClaims.size());

        double totalAmount = carrierClaims.stream().filter(c -> c.getAmount() != null)
                .mapToDouble(Claim::getAmount).sum();
        result.put("totalClaimedAmount", totalAmount);

        long approved = carrierClaims.stream()
                .filter(c -> ClaimStatus.CARRIER_APPROVED.equals(c.getStatus()) ||
                             ClaimStatus.ADMIN_APPROVED.equals(c.getStatus())).count();
        long rejected = carrierClaims.stream()
                .filter(c -> ClaimStatus.REJECTED.equals(c.getStatus())).count();

        result.put("approvedClaims", approved);
        result.put("rejectedClaims", rejected);
        result.put("approvalRate", carrierClaims.isEmpty() ? 0.0 :
                Math.round((double) approved / carrierClaims.size() * 100 * 100.0) / 100.0);

        long highRisk = carrierClaims.stream()
                .filter(c -> com.tpa.enums.RiskLevel.HIGH.equals(c.getRiskLevel())).count();
        result.put("highRiskClaims", highRisk);
        result.put("generatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }
}

