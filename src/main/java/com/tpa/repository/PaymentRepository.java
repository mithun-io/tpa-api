package com.tpa.repository;

import com.tpa.entity.Payment;
import com.tpa.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByClaimId(Long claimId);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.razorpayOrderId = :razorpayOrderId")
    Optional<Payment> findByRazorpayOrderIdWithLock(String razorpayOrderId);

    List<Payment> findAllByUserId(Long userId);

    List<Payment> findAllByStatus(PaymentStatus status);
}
