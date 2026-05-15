package com.tpa.mapper;

import com.tpa.dto.request.payment.CreatePaymentOrderRequest;
import com.tpa.dto.response.payment.PaymentResponse;
import com.tpa.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    PaymentResponse toPaymentResponse(Payment payment);

    List<PaymentResponse> toPaymentResponses(List<Payment> payments);

    Payment toPayment(CreatePaymentOrderRequest createPaymentOrderRequest);
}
