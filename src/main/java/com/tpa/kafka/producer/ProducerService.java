package com.tpa.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.kafka.event.ClaimNotificationEvent;
import com.tpa.kafka.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("taskExecutor")
    public void sendPaymentEvent(PaymentEvent paymentEvent) {
        try {
            String message = objectMapper.writeValueAsString(paymentEvent);
            kafkaTemplate.send("payment-success", message).whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("Kafka sent: {}", message);
                } else {
                    log.error("Kafka send failed: {}", message, exception);
                }
            });
        } catch (Exception e) {
            log.error("Failed to serialize payment event: {}", paymentEvent, e);
        }
    }

    @Async("taskExecutor")
    public void sendClaimNotificationEvent(ClaimNotificationEvent claimNotificationEvent) {
        try {
            String message = objectMapper.writeValueAsString(claimNotificationEvent);
            kafkaTemplate.send("claim-notifications", message).whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("Kafka claim notification sent: {}", message);
                } else {
                    log.error("Kafka claim notification send failed: {}", message, exception);
                }
            });
        } catch (Exception e) {
            log.error("Failed to serialize claim notification event: {}", claimNotificationEvent, e);
        }
    }
}