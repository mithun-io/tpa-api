package com.tpa.service;

import com.tpa.dto.response.payment.PaymentReconciliationResponse;
import com.tpa.dto.response.auth.VerifyAuditResponse;
import com.tpa.entity.AuditLog;
import com.tpa.entity.EventAuditLog;
import com.tpa.entity.PaymentLedger;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PaymentEventType;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {

    void logAction(Long claimId, String action, ClaimStatus previousStatus, ClaimStatus newStatus);

    boolean verifyAuditChain(Long claimId);

    List<AuditLog> getClaimAuditTrail(Long claimId);

    List<AuditLog> getByClaimAndAction(Long claimId, String action);

    List<AuditLog> getAuditsByTimeRange(LocalDateTime from, LocalDateTime to);

    VerifyAuditResponse verifyIntegrity(Long claimId);

    List<EventAuditLog> getEventsByClaimId(Long claimId);

    List<EventAuditLog> getEventsByStage(String stage);

    List<EventAuditLog> getUnprocessedEvents();

    List<PaymentLedger> getPaymentLedger(Long claimId);

    List<PaymentLedger> getPaymentsByPaymentId(Long paymentId);

    List<PaymentLedger> getPaymentsByPaymentEventType(PaymentEventType paymentEventType);

    PaymentReconciliationResponse reconcilePayments();
}