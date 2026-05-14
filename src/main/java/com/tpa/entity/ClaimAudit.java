package com.tpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tpa.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim_audits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Enumerated(EnumType.STRING)
    private ClaimStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus newStatus;

    @Column(nullable = false)
    private String changedBy;

    @Column(length = 1000)
    private String notes;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime changedAt;
}
