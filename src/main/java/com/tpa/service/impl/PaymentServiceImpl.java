package com.tpa.service.impl;

import com.tpa.entity.PaymentLedger;
import com.tpa.repository.PaymentLedgerRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.tpa.dto.request.CreatePaymentOrderRequest;
import com.tpa.dto.request.VerifyPaymentRequest;
import com.tpa.dto.response.PaymentResponse;
import com.tpa.entity.Claim;
import com.tpa.entity.Payment;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PaymentStatus;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.repository.ClaimRepository;
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
import java.util.HexFormat;
import java.util.Map;

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

    @Override
    @Transactional
    public Map<String, Object> createOrder(Long userId, CreatePaymentOrderRequest request) {
        Claim claim = claimRepository.findById(request.claimId()).orElseThrow(() -> new NoResourceFoundException("Claim not found with id: " + request.claimId()));

        if (claim.getStatus() != ClaimStatus.CARRIER_APPROVED && claim.getStatus() != ClaimStatus.ADMIN_APPROVED) {
            throw new IllegalStateException("Payment can only be initiated for ADMIN_APPROVED or CARRIER_APPROVED claims. Current status: " + claim.getStatus());
        }

        paymentRepository.findByClaimId(claim.getId()).ifPresent(existing -> {
            if (existing.getStatus() == PaymentStatus.SUCCESS || existing.getStatus() == PaymentStatus.PAID) {
                throw new IllegalStateException("Payment for this claim has already been completed.");
            }
        });

        try {
            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

            int amountInPaise = (int) (request.amount() * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "TPA-CLM-" + claim.getId());
            orderRequest.put("notes", new JSONObject()
                    .put("claimId", claim.getId())
                    .put("patientName", claim.getPatientName())
                    .put("policyNumber", claim.getPolicyNumber()));

            Order order = client.orders.create(orderRequest);
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

            claim.setStatus(ClaimStatus.PAYMENT_PENDING);
            claimRepository.save(claim);

            // Record ledger entry
            paymentLedgerRepository.save(PaymentLedger.builder()
                    .claimId(claim.getId())
                    .paymentId(payment.getId())
                    .amount(request.amount())
                    .currency("INR")
                    .eventType("PAYMENT_CREATED")
                    .status("CREATED")
                    .razorpayOrderId(razorpayOrderId)
                    .initiatedBy("USER-" + userId)
                    .notes("Razorpay order created")
                    .build());

            log.info("Razorpay order created: {} for claim: {}", razorpayOrderId, claim.getId());

            return Map.of(
                    "orderId", razorpayOrderId,
                    "amount", amountInPaise,
                    "currency", "INR",
                    "key", razorpayKey,
                    "claimId", claim.getId()
            );

        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order for claim {}: {}", request.claimId(), e.getMessage());
            throw new RuntimeException("Payment gateway error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.razorpay_order_id())
                .orElseThrow(() -> new NoResourceFoundException("Payment order not found: " + request.razorpay_order_id()));

        boolean isValid = verifySignature(
                request.razorpay_order_id(),
                request.razorpay_payment_id(),
                request.razorpay_signature()
        );

        if (!isValid) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("Invalid Razorpay signature for order: {}", request.razorpay_order_id());
            throw new SecurityException("Payment signature verification failed. Possible tampered request.");
        }

        payment.setRazorpayPaymentId(request.razorpay_payment_id());
        payment.setRazorpaySignature(request.razorpay_signature());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        Claim claim = claimRepository.findById(payment.getClaimId()).orElseThrow(() -> new NoResourceFoundException("Claim not found"));
        claim.setStatus(ClaimStatus.SETTLED);
        claimRepository.save(claim);

        // Record verified ledger entry
        paymentLedgerRepository.save(PaymentLedger.builder()
                .claimId(payment.getClaimId())
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .eventType("PAYMENT_VERIFIED")
                .status("SUCCESS")
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .notes("Razorpay signature verified — claim SETTLED")
                .initiatedBy("GATEWAY")
                .build());

        log.info("Payment verified successfully for claim {}. Claim marked as SETTLED.", payment.getClaimId());

        return toResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByClaimId(Long claimId) {
        Payment payment = paymentRepository.findByClaimId(claimId).orElseThrow(() -> new NoResourceFoundException("No payment found for claim: " + claimId));
        return toResponse(payment);
    }

    @Override
    @Transactional
    public void initiateInstantPayout(Claim claim) {
        log.info("[INSTANT-PAYOUT] Triggering automated payout for claim #{} (Amount: {})", claim.getId(), claim.getAmount());
        
        // Mock instant payout logic: Create a successful payment record immediately
        Payment payment = Payment.builder()
                .claimId(claim.getId())
                .userId(claim.getUser().getId())
                .amount(claim.getAmount())
                .currency("INR")
                .status(PaymentStatus.SUCCESS)
                .razorpayOrderId("MOCK_INSTANT_" + System.currentTimeMillis())
                .build();
        paymentRepository.save(payment);

        claim.setStatus(ClaimStatus.SETTLED);
        claimRepository.save(claim);
        
        log.info("[INSTANT-PAYOUT] Claim #{} successfully settled via instant payout", claim.getId());
    }

    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpaySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generated = HexFormat.of().formatHex(hash);
            return generated.equals(signature);
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(), p.getClaimId(), p.getAmount(), p.getCurrency(),
                p.getStatus(), p.getRazorpayOrderId(), p.getRazorpayPaymentId(), p.getCreatedAt()
        );
    }
}
