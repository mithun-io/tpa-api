package com.tpa.repository;

import com.tpa.entity.EventAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventAuditLogRepository extends JpaRepository<EventAuditLog, Long> {
    boolean existsByEventId(String eventId);
    Optional<EventAuditLog> findByEventId(String eventId);
    List<EventAuditLog> findByClaimIdOrderByReceivedAtDesc(Long claimId);
    List<EventAuditLog> findByProcessedFalseOrderByReceivedAtAsc();
    List<EventAuditLog> findByStageOrderByReceivedAtDesc(String stage);

    // Kafka Monitor metrics
    long countByProcessedTrue();
    long countByProcessedFalse();
    long countByStage(String stage);
}
