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

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private String companyType;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String taxId;

    @Column(nullable = false)
    private String contactPersonName;

    @Column(nullable = false)
    private String contactPersonPhone;

    private String website;

    private Double aiRiskScore;

    private AiRiskStatus aiRiskStatus;

    private AiRecommendation aiRecommendation;
}
