package com.tpa.entity;

import com.tpa.enums.PolicyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "insurance_products")
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PolicyType policyType;

    @Column(nullable = false)
    private Double coverageAmount;

    @Column(nullable = false)
    private Double premiumAmount;

    @Column(nullable = false)
    private Integer waitingPeriodDays;

    @Column(nullable = false)
    private Boolean active;
}