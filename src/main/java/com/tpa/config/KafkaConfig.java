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

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Consumer Factory
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

    // Producer Factory
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // strongest durability
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Error Handler: Exponential Backoff + DLQ
    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        ExponentialBackOff exponentialBackOff = new ExponentialBackOff(2000L, 2.0);
        exponentialBackOff.setMaxAttempts(3); // 3 retries before DLQ
        exponentialBackOff.setMaxElapsedTime(30000L);

        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, exponentialBackOff);
        defaultErrorHandler.addNotRetryableExceptions(IllegalArgumentException.class); // don't retry bad data

        return defaultErrorHandler;
    }

    // Retry-enabled Listener Container Factory
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> retryKafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory, DefaultErrorHandler defaultErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory);
        concurrentKafkaListenerContainerFactory.setCommonErrorHandler(defaultErrorHandler);
        concurrentKafkaListenerContainerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        concurrentKafkaListenerContainerFactory.setConcurrency(3); // parallel consumer threads

        return concurrentKafkaListenerContainerFactory;
    }

    // Topic Declarations (auto-create with DLQs for all lifecycle topics)
    // Primary lifecycle topics
    @Bean public NewTopic claimUploadedTopic()        { return newTopic("claim-lifecycle.uploaded"); }
    @Bean public NewTopic claimOcrCompletedTopic()    { return newTopic("claim-lifecycle.ocr-completed"); }
    @Bean public NewTopic claimAiDoneTopic()          { return newTopic("claim-lifecycle.ai-done"); }
    @Bean public NewTopic claimRuleEvaluatedTopic()   { return newTopic("claim-lifecycle.rule-evaluated"); }
    @Bean public NewTopic claimAdminApprovedTopic()   { return newTopic("claim-lifecycle.admin-approved"); }
    @Bean public NewTopic claimCarrierApprovedTopic() { return newTopic("claim-lifecycle.carrier-approved"); }
    @Bean public NewTopic claimPaymentInitiatedTopic(){ return newTopic("claim-lifecycle.payment-initiated"); }
    @Bean public NewTopic claimPaymentCompletedTopic(){ return newTopic("claim-lifecycle.payment-completed"); }
    @Bean public NewTopic claimRejectedTopic()        { return newTopic("claim-lifecycle.rejected"); }

    // DLQ topics — all lifecycle topics get a DLQ companion
    @Bean public NewTopic claimUploadedDlqTopic()        { return newDlqTopic("claim-lifecycle.uploaded-dlq"); }
    @Bean public NewTopic claimOcrCompletedDlqTopic()    { return newDlqTopic("claim-lifecycle.ocr-completed-dlq"); }
    @Bean public NewTopic claimAiDoneDlqTopic()          { return newDlqTopic("claim-lifecycle.ai-done-dlq"); }
    @Bean public NewTopic claimRuleEvaluatedDlqTopic()   { return newDlqTopic("claim-lifecycle.rule-evaluated-dlq"); }
    @Bean public NewTopic claimAdminApprovedDlqTopic()   { return newDlqTopic("claim-lifecycle.admin-approved-dlq"); }
    @Bean public NewTopic claimCarrierApprovedDlqTopic() { return newDlqTopic("claim-lifecycle.carrier-approved-dlq"); }
    @Bean public NewTopic claimPaymentInitiatedDlqTopic(){ return newDlqTopic("claim-lifecycle.payment-initiated-dlq"); }
    @Bean public NewTopic claimPaymentCompletedDlqTopic(){ return newDlqTopic("claim-lifecycle.payment-completed-dlq"); }
    @Bean public NewTopic claimRejectedDlqTopic()        { return newDlqTopic("claim-lifecycle.rejected-dlq"); }

    // Supporting topics
    @Bean public NewTopic claimNotificationsTopic()  { return newTopic("claim-notifications"); }
    @Bean public NewTopic claimCreatedTopic()        { return newTopic("claim-created"); }
    @Bean public NewTopic slaEscalationTopic()       { return newTopic("claim-lifecycle.sla-escalated"); }
    @Bean public NewTopic fraudAlertTopic()          { return newTopic("claim-lifecycle.fraud-alert"); }

    private NewTopic newTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1)
                .build();
    }

    private NewTopic newDlqTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1)
                .config(org.apache.kafka.common.config.TopicConfig.RETENTION_MS_CONFIG, "1209600000") // 14 days
                .build();
    }
}