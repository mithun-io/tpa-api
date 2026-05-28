package com.tpa.entity;

import com.tpa.enums.AiRecommendation;
import com.tpa.enums.AiRiskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "carriers")
@AllArgsConstructor
@NoArgsConstructor
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String companyType;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String taxId;

    @Column(nullable = true)
    private String website;

    @Column(name = "ai_risk_score")
    private Double aiRiskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_risk_status")
    private AiRiskStatus aiRiskStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_recommendation")
    private AiRecommendation aiRecommendation;
}
