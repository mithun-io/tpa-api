package com.tpa.controller;

import com.tpa.dto.response.*;
import com.tpa.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('FMG_ADMIN', 'CARRIER_USER')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getDashboard() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard analytics fetched successfully", analyticsService.getDashboardAnalytics(), 200));
    }

    @GetMapping("/fraud/trends")
    public ResponseEntity<ApiResponse<FraudTrendResponse>> getFraudTrends() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Fraud trends fetched successfully", analyticsService.getFraudTrends(), 200));
    }

    @GetMapping("/sla/performance")
    public ResponseEntity<ApiResponse<SlaPerformanceResponse>> getSlaPerformance() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Sla performance fetched successfully", analyticsService.getSlaPerformance(), 200));
    }

    @GetMapping("/leakage")
    public ResponseEntity<ApiResponse<ClaimLeakageResponse>> getClaimLeakage() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Claim leakage analytics fetched successfully", analyticsService.getClaimLeakage(), 200));
    }

    @GetMapping("/hospitals")
    public ResponseEntity<ApiResponse<HospitalAnalyticsResponse>> getHospitalAnalytics() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Hospital analytics fetched successfully", analyticsService.getHospitalAnalytics(), 200));
    }

    @GetMapping("/forecast")
    public ResponseEntity<ApiResponse<ForecastAnalyticsResponse>> getVolumeForecast() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Forecast analytics fetched successfully", analyticsService.getVolumeForecast(), 200));
    }

    @GetMapping("/payments/summary")
    public ResponseEntity<ApiResponse<PaymentSummaryResponse>> getPaymentSummary() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment summary fetched successfully", analyticsService.getPaymentSummary(), 200));
    }

    @GetMapping("/loss-ratio")
    public ResponseEntity<ApiResponse<LossRatioResponse>> getLossRatio() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Loss ratio fetched successfully", analyticsService.getLossRatio(), 200));
    }

    @GetMapping("/carrier/{carrierName}/summary")
    public ResponseEntity<ApiResponse<CarrierAnalyticsResponse>> getCarrierSummary(@PathVariable String carrierName) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Carrier analytics fetched successfully", analyticsService.getCarrierSummary(carrierName), 200));
    }
}