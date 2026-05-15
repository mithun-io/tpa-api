package com.tpa.service;

import com.tpa.dto.response.*;

public interface AnalyticsService {

    AnalyticsResponse getDashboardAnalytics();

    FraudTrendResponse getFraudTrends();

    SlaPerformanceResponse getSlaPerformance();

    ClaimLeakageResponse getClaimLeakage();

    HospitalAnalyticsResponse getHospitalAnalytics();

    ForecastAnalyticsResponse getVolumeForecast();

    PaymentSummaryResponse getPaymentSummary();

    LossRatioResponse getLossRatio();

    CarrierAnalyticsResponse getCarrierSummary(String carrierName);
}
