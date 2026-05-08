package com.tpa.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // ── Consumer Factory ─────────────────────────────────────────────────────

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "tpa-pipeline-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // manual ack for retry
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // ── Producer Factory ─────────────────────────────────────────────────────

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");           // strongest durability
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ── Error Handler: Exponential Backoff + DLQ ─────────────────────────────

    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer dlqRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        ExponentialBackOff backOff = new ExponentialBackOff(2000L, 2.0);
        backOff.setMaxAttempts(3);      // 3 retries before DLQ
        backOff.setMaxElapsedTime(30000L);
        DefaultErrorHandler handler = new DefaultErrorHandler(dlqRecoverer, backOff);
        handler.addNotRetryableExceptions(IllegalArgumentException.class); // don't retry bad data
        return handler;
    }

    // ── Retry-enabled Listener Container Factory ──────────────────────────────

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> retryKafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setConcurrency(3); // parallel consumer threads
        return factory;
    }

    // ── Topic Declarations (auto-create) ─────────────────────────────────────

    @Bean public NewTopic claimUploadedTopic()       { return topic("claim-lifecycle.uploaded"); }
    @Bean public NewTopic claimOcrCompletedTopic()   { return topic("claim-lifecycle.ocr-completed"); }
    @Bean public NewTopic claimAiDoneTopic()         { return topic("claim-lifecycle.ai-done"); }
    @Bean public NewTopic claimRuleEvaluatedTopic()  { return topic("claim-lifecycle.rule-evaluated"); }
    @Bean public NewTopic claimAdminApprovedTopic()  { return topic("claim-lifecycle.admin-approved"); }
    @Bean public NewTopic claimCarrierApprovedTopic(){ return topic("claim-lifecycle.carrier-approved"); }
    @Bean public NewTopic claimPaymentInitiatedTopic(){ return topic("claim-lifecycle.payment-initiated"); }
    @Bean public NewTopic claimPaymentCompletedTopic(){ return topic("claim-lifecycle.payment-completed"); }
    @Bean public NewTopic claimRejectedTopic()       { return topic("claim-lifecycle.rejected"); }

    // DLQ topics
    @Bean public NewTopic claimUploadedDlqTopic()    { return topic("claim-lifecycle.uploaded-dlq"); }
    @Bean public NewTopic claimNotificationsTopic()  { return topic("claim-notifications"); }
    @Bean public NewTopic claimCreatedTopic()        { return topic("claim-created"); }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1)
                .build();
    }
}