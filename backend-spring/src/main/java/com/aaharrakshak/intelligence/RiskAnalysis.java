package com.aaharrakshak.intelligence;

import com.aaharrakshak.complaint.Complaint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "risk_analyses")
public class RiskAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(nullable = false, length = 2000)
    private String reasons;

    @Column(nullable = false, length = 120)
    private String adapterName;

    @Column(nullable = false, length = 500)
    private String imageSafetyNote;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected RiskAnalysis() {
    }

    public RiskAnalysis(
            Complaint complaint,
            Integer score,
            RiskLevel riskLevel,
            String reasons,
            String adapterName,
            String imageSafetyNote) {
        this.complaint = complaint;
        this.score = score;
        this.riskLevel = riskLevel;
        this.reasons = reasons;
        this.adapterName = adapterName;
        this.imageSafetyNote = imageSafetyNote;
    }

    public Long getId() {
        return id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public Integer getScore() {
        return score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getReasons() {
        return reasons;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public String getImageSafetyNote() {
        return imageSafetyNote;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
