package com.tpa.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.tpa.dto.request.payment.CreatePaymentOrderRequest;
import com.tpa.dto.request.payment.VerifyPaymentRequest;
import com.tpa.dto.response.payment.PaymentOrderResponse;
import com.tpa.dto.response.payment.PaymentResponse;
import com.tpa.entity.Claim;
import com.tpa.entity.Payment;
import com.tpa.entity.PaymentLedger;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PaymentEventType;
import com.tpa.enums.PaymentStatus;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.PaymentLedgerRepository;
import com.tpa.repository.PaymentRepository;
import com.tpa.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final ClaimRepository claimRepository;

    private final PaymentLedgerRepository paymentLedgerRepository;

    @Value("${razorpay.api.key}")
    private String razorpayKey;

    @Value("${razorpay.api.secret}")
    private String razorpaySecret;

    private void validatePaymentEligibility(Claim claim) {
        if (claim.getClaimStatus() != ClaimStatus.CARRIER_APPROVED && claim.getClaimStatus() != ClaimStatus.ADMIN_APPROVED) {
            throw new IllegalStateException("Payment allowed only for approved claims");
        }
    }

    private void validateDuplicateSuccessfulPayment(Long claimId) {
        paymentRepository.findByClaimId(claimId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.SUCCESS || payment.getStatus() == PaymentStatus.PAID) {
                throw new IllegalStateException("Payment already completed for this claim");
            }
        });
    }

    private JSONObject buildOrderRequest(Claim claim, int amountInPaise) {
        JSONObject notes = new JSONObject().put("claimId", claim.getId()).put("patientName", claim.getPatientName()).put("policyNumber", claim.getPolicyNumber());
        return new JSONObject().put("amount", amountInPaise)
                .put("currency", "INR")
                .put("receipt", "TPA-CLM-" + claim.getId())
                .put("notes", notes);
    }

    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpaySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = HexFormat.of().formatHex(hash);

            return generatedSignature.equals(signature);
        } catch (Exception exception) {
            log.error("Signature verification failed", exception);
            return false;
        }
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getClaimId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getCreatedAt());
    }

    private void saveLedgerEntry(Long claimId,
                                 Long paymentId,
                                 Double amount,
                                 String currency,
                                 PaymentEventType paymentEventType,
                                 PaymentStatus paymentStatus,
                                 String razorpayOrderId,
                                 String razorpayPaymentId,
                                 String initiatedBy,
                                 String notes) {
        PaymentLedger paymentLedger = PaymentLedger.builder()
                .claimId(claimId).paymentId(paymentId)
                .amount(amount).currency(currency)
                .paymentEventType(paymentEventType)
                .paymentStatus(paymentStatus)
                .razorpayOrderId(razorpayOrderId)
                .razorpayPaymentId(razorpayPaymentId)
                .initiatedBy(initiatedBy)
                .notes(notes)
                .build();

        paymentLedgerRepository.save(paymentLedger);
    }

    @Override
    @Transactional
    public PaymentOrderResponse createOrder(Long userId, CreatePaymentOrderRequest request) {
        Claim claim = claimRepository.findById(request.claimId()).orElseThrow(() -> new NoResourceFoundException("Claim not found with id: " + request.claimId()));

        validatePaymentEligibility(claim);
        validateDuplicateSuccessfulPayment(claim.getId());

        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);

            int amountInPaise = (int) (request.amount() * 100);

            JSONObject orderRequest = buildOrderRequest(claim, amountInPaise);

            Order order = razorpayClient.orders.create(orderRequest);

            String razorpayOrderId = order.get("id");

            Payment payment = Payment.builder()
                    .claimId(claim.getId())
                    .userId(userId)
                    .amount(request.amount())
                    .currency("INR")
                    .status(PaymentStatus.CREATED)
                    .razorpayOrderId(razorpayOrderId)
                    .build();

            paymentRepository.save(payment);

            claim.setClaimStatus(ClaimStatus.PAYMENT_PENDING);
            claimRepository.save(claim);

            saveLedgerEntry(claim.getId(), payment.getId(), request.amount(), "INR", PaymentEventType.PAYMENT_CREATED, PaymentStatus.CREATED, razorpayOrderId, null, "USER-" + userId, "Razorpay order created");

            log.info("Payment order created for claim {}", claim.getId());
            return PaymentOrderResponse.builder()
                    .orderId(razorpayOrderId)
                    .amount(amountInPaise)
                    .currency("INR")
                    .key(razorpayKey)
                    .claimId(claim.getId())
                    .build();

        } catch (RazorpayException exception) {
            log.error("Failed to create Razorpay order", exception);
            throw new RuntimeException("Payment gateway error: " + exception.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.razorpay_order_id()).orElseThrow(() -> new NoResourceFoundException("Payment order not found"));

        boolean validSignature = verifySignature(request.razorpay_order_id(), request.razorpay_payment_id(), request.razorpay_signature());
        if (!validSignature) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            throw new SecurityException("Payment signature verification failed");
        }

        payment.setRazorpayPaymentId(request.razorpay_payment_id());
        payment.setRazorpaySignature(request.razorpay_signature());
        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);

        Claim claim = claimRepository.findById(payment.getClaimId()).orElseThrow(() -> new NoResourceFoundException("Claim not found"));
        claim.setClaimStatus(ClaimStatus.SETTLED);
        claimRepository.save(claim);

        saveLedgerEntry(payment.getClaimId(), payment.getId(), payment.getAmount(), payment.getCurrency(), PaymentEventType.PAYMENT_VERIFIED, PaymentStatus.SUCCESS, payment.getRazorpayOrderId(), payment.getRazorpayPaymentId(), "GATEWAY", "Payment verified successfully");

        log.info("Payment verified successfully for claim {}", claim.getId());
        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByClaimId(Long claimId) {
        Payment payment = paymentRepository.findByClaimId(claimId).orElseThrow(() -> new NoResourceFoundException("No payment found for claim: " + claimId));
        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public void initiateInstantPayout(Claim claim) {
        log.info("[INSTANT-PAYOUT] Processing payout for claim {}", claim.getId());

        Payment payment = Payment.builder()
                .claimId(claim.getId())
                .userId(claim.getUser().getId())
                .amount(claim.getAmount())
                .currency("INR")
                .status(PaymentStatus.SUCCESS)
                .razorpayOrderId("MOCK_INSTANT_" + System.currentTimeMillis())
                .razorpayPaymentId("MOCK_PAYMENT_" + System.currentTimeMillis())
                .build();

        paymentRepository.save(payment);

        claim.setClaimStatus(ClaimStatus.SETTLED);
        claim.setProcessedDate(LocalDateTime.now());
        claimRepository.save(claim);

        saveLedgerEntry(
                claim.getId(),
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                PaymentEventType.PAYMENT_SUCCESS,
                PaymentStatus.SUCCESS,
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                "SYSTEM",
                "Instant payout completed");

        log.info("[INSTANT-PAYOUT] Claim {} settled successfully", claim.getId());
    }
}