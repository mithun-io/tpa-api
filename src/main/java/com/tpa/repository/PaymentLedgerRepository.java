package com.tpa.repository;

import com.tpa.entity.PaymentLedger;
import com.tpa.enums.PaymentEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentLedgerRepository
        extends JpaRepository<PaymentLedger, Long> {

    List<PaymentLedger> findByClaimIdOrderByCreatedAtAsc(Long claimId);

    List<PaymentLedger> findByPaymentIdOrderByCreatedAtAsc(Long paymentId);

    List<PaymentLedger> findByPaymentEventTypeOrderByCreatedAtDesc(PaymentEventType paymentEventType);

    @Query("""
            SELECT SUM(p.amount)
            FROM PaymentLedger p
            WHERE p.paymentEventType =
            com.tpa.enums.PaymentEventType.PAYMENT_SUCCESS
            """)
    Double sumVerifiedPayments();

    @Query("""
            SELECT COUNT(p)
            FROM PaymentLedger p
            WHERE p.paymentEventType = :eventType
            """)
    long countByPaymentEventType(@Param("eventType") PaymentEventType paymentEventType);
}