package com.tpa.repository;

import com.tpa.entity.PaymentLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentLedgerRepository extends JpaRepository<PaymentLedger, Long> {

    List<PaymentLedger> findByClaimIdOrderByCreatedAtAsc(Long claimId);

    List<PaymentLedger> findByPaymentIdOrderByCreatedAtAsc(Long paymentId);

    List<PaymentLedger> findByEventTypeOrderByCreatedAtDesc(String eventType);

    @Query("SELECT SUM(p.amount) FROM PaymentLedger p WHERE p.eventType = 'PAYMENT_VERIFIED'")
    Double sumVerifiedPayments();
}
