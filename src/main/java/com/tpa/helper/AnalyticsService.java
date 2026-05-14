package com.tpa.helper;

import com.tpa.enums.ClaimStatus;
import com.tpa.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ClaimRepository claimRepository;

    public Map<String, Object> getDashboardAnalytics() {
        Map<String, Object> response = new HashMap<>();

        List<Object[]> statusCountsObj = claimRepository.countClaimsByStatus();
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] obj : statusCountsObj) {
            try {
                // obj[0] may be a ClaimStatus enum OR a String depending on JPA dialect
                String statusName = (obj[0] instanceof ClaimStatus cs)
                        ? cs.name()
                        : obj[0].toString();
                statusCounts.put(statusName, ((Number) obj[1]).longValue());
            } catch (Exception ignored) {
                // skip malformed rows
            }
        }
        response.put("statusDistribution", statusCounts);

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> dailyCountsObj = claimRepository.countClaimsPerDay(thirtyDaysAgo);
        List<Map<String, Object>> dailyCounts = dailyCountsObj.stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", obj[0].toString());
            map.put("count", ((Number) obj[1]).longValue());
            return map;
        }).collect(Collectors.toList());
        response.put("claimsPerDay", dailyCounts);

        // Derive totalClaims from status distribution
        long totalClaims = statusCounts.values().stream().mapToLong(Long::longValue).sum();
        response.put("totalClaims", totalClaims);

        Double totalPayout = claimRepository.sumApprovedClaimAmount();
        response.put("totalApprovedPayout", totalPayout != null ? totalPayout : 0.0);
        response.put("totalClaimAmount", totalPayout != null ? totalPayout : 0.0);

        return response;
    }
}
