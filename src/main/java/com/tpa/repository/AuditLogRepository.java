package com.tpa.repository;

import com.tpa.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByClaimIdOrderByIdAsc(Long claimId);

    List<AuditLog> findByClaimIdOrderByIdDesc(Long claimId);

    Optional<AuditLog> findTopByClaimIdOrderByIdDesc(Long claimId);

    Page<AuditLog> findByActionOrderByTimestampDesc(String action, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :from AND :to ORDER BY a.timestamp DESC")
    List<AuditLog> findByTimestampBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT a FROM AuditLog a WHERE a.claimId = :claimId AND a.action = :action ORDER BY a.timestamp DESC")
    List<AuditLog> findByClaimIdAndAction(Long claimId, String action);
}
