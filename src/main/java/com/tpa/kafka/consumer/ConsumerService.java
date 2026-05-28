package com.tpa.kafka.consumer;

import com.tpa.helper.EmailService;
import com.tpa.kafka.event.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumerService {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-success", groupId = "tpa-group", containerFactory = "retryKafkaListenerContainerFactory")
    @Transactional
    public void consume(String message) throws Exception {
        PaymentEvent paymentEvent = objectMapper.readValue(message, PaymentEvent.class);

        log.info("Kafka received: {}", paymentEvent);
        emailService.sendPaymentConfirmation(paymentEvent.getEmail(), paymentEvent.getOrderId(), paymentEvent.getAmount());
    }
}