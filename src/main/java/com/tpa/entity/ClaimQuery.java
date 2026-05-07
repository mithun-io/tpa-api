package com.tpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim_queries")
public class ClaimQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Column(nullable = false)
    private String senderUsername;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean isCarrierQuery; // true if carrier asking TPA, false if TPA responding

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public ClaimQuery() {}

    public ClaimQuery(Claim claim, String senderUsername, String message, boolean isCarrierQuery) {
        this.claim = claim;
        this.senderUsername = senderUsername;
        this.message = message;
        this.isCarrierQuery = isCarrierQuery;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isCarrierQuery() { return isCarrierQuery; }
    public void setCarrierQuery(boolean isCarrierQuery) { this.isCarrierQuery = isCarrierQuery; }
}
