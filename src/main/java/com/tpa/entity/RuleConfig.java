package com.tpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rule_configs")
public class RuleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ruleKey; // e.g., MAX_CLAIM_AMOUNT_AUTO_APPROVE

    @Column(nullable = false)
    private String ruleValue;

    private String description;

    public RuleConfig() {}

    public RuleConfig(String ruleKey, String ruleValue, String description) {
        this.ruleKey = ruleKey;
        this.ruleValue = ruleValue;
        this.description = description;
    }

    public static RuleConfigBuilder builder() { return new RuleConfigBuilder(); }

    public static class RuleConfigBuilder {
        private String ruleKey;
        private String ruleValue;
        private String description;

        public RuleConfigBuilder ruleKey(String ruleKey) { this.ruleKey = ruleKey; return this; }
        public RuleConfigBuilder ruleValue(String ruleValue) { this.ruleValue = ruleValue; return this; }
        public RuleConfigBuilder description(String description) { this.description = description; return this; }
        public RuleConfig build() { return new RuleConfig(ruleKey, ruleValue, description); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRuleKey() { return ruleKey; }
    public void setRuleKey(String ruleKey) { this.ruleKey = ruleKey; }
    public String getRuleValue() { return ruleValue; }
    public void setRuleValue(String ruleValue) { this.ruleValue = ruleValue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
