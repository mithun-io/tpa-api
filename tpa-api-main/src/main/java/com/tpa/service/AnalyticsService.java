package com.tpa.service;

import com.tpa.dto.response.analytics.*;
import com.tpa.dto.response.claim.ClaimLeakageResponse;
import com.tpa.dto.response.payment.PaymentSummaryResponse;

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
