package com.tpa.entity;

import com.tpa.enums.AiRecommendation;
import com.tpa.enums.AiRiskStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class CarrierRiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    private Double aiRiskScore;

    @Enumerated(EnumType.STRING)
    private AiRiskStatus aiRiskStatus;

    @Enumerated(EnumType.STRING)
    private AiRecommendation aiRecommendation;

    private String aiReasoning;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime analyzedAt;
}