package com.tpa.service.impl;

import com.tpa.dto.response.AnalyticsResponse;
import com.tpa.enums.ClaimStatus;
import com.tpa.repository.ClaimRepository;
import com.tpa.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ClaimRepository claimRepository;

    @Override
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

        List<AnalyticsResponse.ClaimsPerDayResponse> claimsPerDay = dailyCountsObj.stream().map(o -> AnalyticsResponse.ClaimsPerDayResponse
                        .builder()
                        .date(o[0].toString())
                        .count(((Number) o[1]).longValue())
                        .build()
                ).toList();

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
}