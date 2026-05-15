package com.tpa.service;

import com.tpa.dto.response.kafka.KafkaDlqResponse;
import com.tpa.dto.response.kafka.KafkaPendingEventsResponse;
import com.tpa.dto.response.kafka.KafkaPipelineHealthResponse;
import com.tpa.dto.response.kafka.KafkaRetryResponse;
import com.tpa.dto.response.kafka.KafkaTopicsResponse;

public interface KafkaMonitorService {

    KafkaTopicsResponse getTopics();

    KafkaPipelineHealthResponse getPipelineHealth();

    KafkaDlqResponse getDlqMessages(int page, int size);

    KafkaPendingEventsResponse getPendingEvents();

    KafkaRetryResponse retryDlqEvent(String eventId);
}