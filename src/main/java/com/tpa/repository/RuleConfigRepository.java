package com.tpa.repository;

import com.tpa.entity.RuleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleConfigRepository extends JpaRepository<RuleConfig, Long> {
    Optional<RuleConfig> findByRuleKey(String ruleKey);
    List<RuleConfig> findByActiveTrueOrderByPriorityAsc();
    List<RuleConfig> findByCategoryAndActiveTrueOrderByPriorityAsc(String category);
    boolean existsByRuleKey(String ruleKey);
}
