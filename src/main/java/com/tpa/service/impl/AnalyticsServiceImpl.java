package com.tpa.service.impl;

import com.tpa.dto.response.*;
import com.tpa.entity.Claim;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PaymentEventType;
import com.tpa.enums.RiskLevel;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.PaymentLedgerRepository;
import com.tpa.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ClaimRepository claimRepository;
    private final PaymentLedgerRepository paymentLedgerRepository;

    private long countRisk(List<Claim> claims, RiskLevel riskLevel) {
        return claims.stream()
                .filter(c -> c.getRiskLevel() == riskLevel)
                .count();
    }

    private double calculateAverageFraudScore(List<Claim> claims) {
        return Math.round(claims.stream()
                .filter(c -> c.getRiskScore() != null)
                .mapToDouble(Claim::getRiskScore)
                .average()
                .orElse(0.0)
                * 100.0) / 100.0;
    }

    private double calculateFraudRate(int totalClaims, long highRisk, long mediumRisk) {
        if (totalClaims == 0) {
            return 0.0;
        }
        return Math.round(((double) (highRisk + mediumRisk) / totalClaims) * 100 * 100.0) / 100.0;
    }

    private Map<String, Long> buildHospitalRiskDistribution(List<Claim> claims) {
        return claims.stream()
                .filter(c -> c.getRiskLevel() == RiskLevel.HIGH && c.getHospitalName() != null)
                .collect(Collectors.groupingBy(Claim::getHospitalName, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponse getDashboardAnalytics() {
        List<Object[]> countClaims = claimRepository.countClaimsByStatus();
        Map<String, Long> map = new HashMap<>();

        for (Object[] o : countClaims) {
            try {
                String statusName = (o[0] instanceof ClaimStatus claimStatus) ? claimStatus.name() : o[0].toString();
                map.put(statusName, ((Number) o[1]).longValue());
            } catch (Exception ignored) {
            }
        }

        LocalDateTime localDateTime = LocalDateTime.now().minusDays(30);
        List<Object[]> dailyCountsObj = claimRepository.countClaimsPerDay(localDateTime);

        List<AnalyticsResponse.ClaimsPerDayResponse> claimsPerDay = dailyCountsObj.stream()
                .map(o -> AnalyticsResponse.ClaimsPerDayResponse
                        .builder()
                        .date(o[0].toString())
                        .count(((Number) o[1]).longValue())
                        .build()).toList();

        long totalClaims = map.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        Double totalPayout = claimRepository.sumApprovedClaimAmount();
        if (totalPayout == null) {
            totalPayout = 0.0;
        }

        return AnalyticsResponse.builder()
                .totalClaims(totalClaims)
                .totalApprovedPayout(totalPayout)
                .totalClaimAmount(totalPayout)
                .statusDistribution(map)
                .claimsPerDay(claimsPerDay)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FraudTrendResponse getFraudTrends() {
        List<Claim> allClaims = claimRepository.findAll();

        long highRisk = countRisk(allClaims, RiskLevel.HIGH);
        long mediumRisk = countRisk(allClaims, RiskLevel.MEDIUM);

        long lowRisk = allClaims.stream()
                .filter(c -> c.getRiskLevel() == RiskLevel.LOW || c.getRiskLevel() == null)
                .count();

        Map<String, Long> hospitalRisk = buildHospitalRiskDistribution(allClaims);

        double averageFraudScore = calculateAverageFraudScore(allClaims);

        return FraudTrendResponse.builder()
                .riskDistribution(Map.of("HIGH", highRisk, "MEDIUM", mediumRisk, "LOW", lowRisk))
                .fraudRate(calculateFraudRate(allClaims.size(), highRisk, mediumRisk))
                .topRiskHospitals(hospitalRisk)
                .averageFraudScore(averageFraudScore)
                .generatedAt(LocalDateTime.now().toString())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SlaPerformanceResponse getSlaPerformance() {
        List<Claim> allClaims = claimRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        long total = allClaims.size();
        long breached = allClaims.stream()
                .filter(c -> c.getSlaDeadline() != null && c.getSlaDeadline().isBefore(now))
                .filter(c -> !List.of(ClaimStatus.SETTLED, ClaimStatus.REJECTED).contains(c.getClaimStatus()))
                .count();

        long escalated = allClaims.stream()
                .filter(c -> Boolean.TRUE.equals(c.getEscalated()))
                .count();

        double complianceRate = total == 0
                ? 100.0
                : Math.round(((double) (total - breached) / total) * 100 * 100.0) / 100.0;

        double avgProcessingHours = Math.round(allClaims.stream()
                .filter(c -> c.getProcessedDate() != null && c.getCreatedDate() != null)
                .mapToLong(c -> Duration.between(c.getCreatedDate(), c.getProcessedDate()).toHours())
                .average()
                .orElse(0.0) * 10.0) / 10.0;

        return SlaPerformanceResponse.builder()
                .totalClaims(total)
                .withinSla(total - breached)
                .slaBreached(breached)
                .escalated(escalated)
                .slaComplianceRate(complianceRate)
                .avgProcessingHours(avgProcessingHours)
                .generatedAt(now.toString())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimLeakageResponse getClaimLeakage() {
        List<Claim> allClaims = claimRepository.findAll();

        double totalClaimed = allClaims.stream()
                .filter(c -> c.getAmount() != null)
                .mapToDouble(Claim::getAmount)
                .sum();
        double approvedPayout = Optional.ofNullable(claimRepository.sumApprovedClaimAmount()).orElse(0.0);

        long mismatchCount = allClaims.stream()
                .filter(c -> c.getAmount() != null && c.getTotalBillAmount() != null)
                .filter(c -> !c.getAmount().equals(c.getTotalBillAmount()))
                .count();

        double leakageAmount = totalClaimed - approvedPayout;
        double leakageRate = totalClaimed == 0
                ? 0.0
                : Math.round((leakageAmount / totalClaimed) * 100 * 100.0) / 100.0;

        return ClaimLeakageResponse.builder()
                .totalClaimedAmount(totalClaimed)
                .totalApprovedPayout(approvedPayout)
                .leakageAmount(leakageAmount)
                .leakageRate(leakageRate)
                .amountMismatchCount(mismatchCount)
                .generatedAt(LocalDateTime.now().toString())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public HospitalAnalyticsResponse getHospitalAnalytics() {
        List<Claim> allClaims = claimRepository.findAll();

        Map<String, Long> claimsPerHospital = allClaims.stream()
                .filter(c -> c.getHospitalName() != null)
                .collect(Collectors.groupingBy(Claim::getHospitalName, Collectors.counting()));

        Map<String, Double> avgAmountPerHospital = allClaims.stream()
                .filter(c -> c.getHospitalName() != null && c.getAmount() != null)
                .collect(Collectors.groupingBy(Claim::getHospitalName, Collectors.averagingDouble(Claim::getAmount)));

        return HospitalAnalyticsResponse.builder()
                .topHospitalsByVolume(claimsPerHospital.entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(10)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new)))
                .topHospitalsByAmount(avgAmountPerHospital.entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .limit(10)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new)))
                .totalUniqueHospitals(claimsPerHospital.size())
                .generatedAt(LocalDateTime.now().toString())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ForecastAnalyticsResponse getVolumeForecast() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<Object[]> dailyCounts = claimRepository.countClaimsPerDay(thirtyDaysAgo);

        long totalLast30 = dailyCounts.stream()
                .mapToLong(r -> ((Number) r[1]).longValue())
                .sum();

        double dailyAverage = totalLast30 / 30.0;

        List<Map<String, Object>> historicalData = dailyCounts.stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", r[0].toString());
                    map.put("count", ((Number) r[1]).longValue());
                    return map;
                }).toList();

        return ForecastAnalyticsResponse.builder()
                .dailyAverageLast30Days(Math.round(dailyAverage * 10.0) / 10.0)
                .forecastNext7Days(Math.round(dailyAverage * 7))
                .forecastNext30Days(Math.round(dailyAverage * 30))
                .historicalData(historicalData)
                .generatedAt(LocalDateTime.now().toString())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentSummaryResponse getPaymentSummary() {
        Double totalSettled = paymentLedgerRepository.sumVerifiedPayments();

        long successCount = paymentLedgerRepository.countByPaymentEventType(PaymentEventType.PAYMENT_SUCCESS);
        long failedCount = paymentLedgerRepository.countByPaymentEventType(PaymentEventType.PAYMENT_FAILED);
        long total = successCount + failedCount;

        double successRate = total == 0
                ? 100.0
                : Math.round(((double) successCount / total) * 100 * 100.0) / 100.0;

        return PaymentSummaryResponse.builder().totalSettledAmount(totalSettled != null ? totalSettled : 0.0)
                .totalSuccessfulSettlements(successCount)
                .totalFailedPayments(failedCount)
                .successRate(successRate)
                .generatedAt(LocalDateTime.now().toString())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LossRatioResponse getLossRatio() {
        List<Claim> allClaims = claimRepository.findAll();

        Double totalPaid = paymentLedgerRepository.sumVerifiedPayments();

        if (totalPaid == null) {
            totalPaid = 0.0;
        }

        double estimatedPremiumPool = allClaims.stream()
                .filter(c -> c.getAmount() != null)
                .mapToDouble(Claim::getAmount)
                .sum() * 0.3;

        double lossRatio = estimatedPremiumPool == 0
                ? 0.0
                : Math.round((totalPaid / estimatedPremiumPool) * 100 * 100.0) / 100.0;

        long settledClaims = allClaims.stream()
                .filter(c -> ClaimStatus.SETTLED.equals(c.getClaimStatus()))
                .count();

        long rejectedClaims = allClaims.stream()
                .filter(c -> ClaimStatus.REJECTED.equals(c.getClaimStatus()))
                .count();

        String lossRatioStatus = lossRatio > 100
                ? "LOSS"
                : lossRatio > 75
                  ? "HIGH"
                  : lossRatio > 50
                    ? "MODERATE"
                    : "HEALTHY";

        return LossRatioResponse.builder()
                .totalClaimsPaid(totalPaid)
                .estimatedPremiumPool(Math.round(estimatedPremiumPool * 100.0) / 100.0)
                .lossRatioPercent(lossRatio)
                .lossRatioStatus(lossRatioStatus)
                .settledClaims(settledClaims)
                .rejectedClaims(rejectedClaims)
                .totalClaims(allClaims.size())
                .generatedAt(LocalDateTime.now().toString())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierAnalyticsResponse getCarrierSummary(String carrierName) {
        List<Claim> carrierClaims = claimRepository.findAll()
                .stream()
                .filter(c -> carrierName.equalsIgnoreCase(c.getCarrierName()))
                .toList();

        double totalClaimedAmount = carrierClaims.stream()
                .filter(c -> c.getAmount() != null)
                .mapToDouble(Claim::getAmount)
                .sum();

        long approvedClaims = carrierClaims.stream()
                .filter(c -> ClaimStatus.CARRIER_APPROVED.equals(c.getClaimStatus()) || ClaimStatus.ADMIN_APPROVED.equals(c.getClaimStatus()))
                .count();

        long rejectedClaims = carrierClaims.stream()
                .filter(c -> ClaimStatus.REJECTED.equals(c.getClaimStatus()))
                .count();

        double approvalRate = carrierClaims.isEmpty() ? 0.0 : Math.round(((double) approvedClaims / carrierClaims.size()) * 100 * 100.0) / 100.0;

        long highRiskClaims = carrierClaims.stream().filter(c -> RiskLevel.HIGH.equals(c.getRiskLevel()))
                .count();

        return CarrierAnalyticsResponse.builder()
                .carrier(carrierName)
                .totalClaims(carrierClaims.size())
                .totalClaimedAmount(totalClaimedAmount)
                .approvedClaims(approvedClaims)
                .rejectedClaims(rejectedClaims)
                .approvalRate(approvalRate)
                .highRiskClaims(highRiskClaims)
                .generatedAt(LocalDateTime.now().toString())
                .build();
    }
}