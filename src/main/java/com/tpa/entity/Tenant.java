package com.tpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenants", indexes = {
        @Index(name = "idx_tenant_slug", columnList = "slug", unique = true),
        @Index(name = "idx_tenant_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /** URL-safe slug used as tenant identifier in requests (header/subdomain). */
    @Column(nullable = false, unique = true)
    private String slug;

    /** Contact email for the tenant organization. */
    private String contactEmail;

    /** Optional custom logo URL for white-labeling. */
    private String logoUrl;

    /** JSON blob for tenant-specific feature flags and branding config. */
    @Column(columnDefinition = "TEXT")
    private String configJson;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /** Max claims allowed per month (0 = unlimited). */
    @Column(nullable = false)
    @Builder.Default
    private Integer claimsQuotaMonthly = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
