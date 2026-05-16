package com.tpa.repository;

import com.tpa.entity.RuleExecutionAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleExecutionAuditRepository extends JpaRepository<RuleExecutionAudit, Long> {

    List<RuleExecutionAudit> findByClaimIdOrderByExecutedAtDesc(Long claimId);

    Page<RuleExecutionAudit> findByRuleKeyOrderByExecutedAtDesc(String ruleKey, Pageable pageable);

    List<RuleExecutionAudit> findBySimulationTrueOrderByExecutedAtDesc();
}
