package com.tpa.mapper;

import com.tpa.dto.request.payment.CreatePaymentOrderRequest;
import com.tpa.dto.response.payment.PaymentResponse;
import com.tpa.entity.Payment;
import com.tpa.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-16T20:00:35+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Eclipse Adoptium)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentResponse toPaymentResponse(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        Long id = null;
        Long claimId = null;
        Double amount = null;
        String currency = null;
        PaymentStatus status = null;
        String razorpayOrderId = null;
        String razorpayPaymentId = null;
        LocalDateTime createdAt = null;

        id = payment.getId();
        claimId = payment.getClaimId();
        amount = payment.getAmount();
        currency = payment.getCurrency();
        status = payment.getStatus();
        razorpayOrderId = payment.getRazorpayOrderId();
        razorpayPaymentId = payment.getRazorpayPaymentId();
        createdAt = payment.getCreatedAt();

        PaymentResponse paymentResponse = new PaymentResponse( id, claimId, amount, currency, status, razorpayOrderId, razorpayPaymentId, createdAt );

        return paymentResponse;
    }

    @Override
    public List<PaymentResponse> toPaymentResponses(List<Payment> payments) {
        if ( payments == null ) {
            return null;
        }

        List<PaymentResponse> list = new ArrayList<PaymentResponse>( payments.size() );
        for ( Payment payment : payments ) {
            list.add( toPaymentResponse( payment ) );
        }

        return list;
    }

    @Override
    public Payment toPayment(CreatePaymentOrderRequest createPaymentOrderRequest) {
        if ( createPaymentOrderRequest == null ) {
            return null;
        }

        Payment.PaymentBuilder payment = Payment.builder();

        payment.claimId( createPaymentOrderRequest.claimId() );
        payment.amount( createPaymentOrderRequest.amount() );

        return payment.build();
    }
}
