package com.aaharrakshak.intelligence;

import com.aaharrakshak.company.Company;
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
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "vendor_trust_scores")
public class VendorTrustScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal inspectionPoints;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal labPoints;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal recallPoints;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal reviewPoints;

    @Column(nullable = false)
    private Integer reviewCount;

    @Column(nullable = false, length = 1600)
    private String explanation;

    @Column(nullable = false)
    private Instant recalculatedAt = Instant.now();

    protected VendorTrustScore() {
    }

    public VendorTrustScore(
            Company company,
            BigDecimal score,
            RiskLevel riskLevel,
            BigDecimal inspectionPoints,
            BigDecimal labPoints,
            BigDecimal recallPoints,
            BigDecimal reviewPoints,
            Integer reviewCount,
            String explanation) {
        this.company = company;
        update(score, riskLevel, inspectionPoints, labPoints, recallPoints, reviewPoints, reviewCount, explanation);
    }

    public void update(
            BigDecimal score,
            RiskLevel riskLevel,
            BigDecimal inspectionPoints,
            BigDecimal labPoints,
            BigDecimal recallPoints,
            BigDecimal reviewPoints,
            Integer reviewCount,
            String explanation) {
        this.score = score;
        this.riskLevel = riskLevel;
        this.inspectionPoints = inspectionPoints;
        this.labPoints = labPoints;
        this.recallPoints = recallPoints;
        this.reviewPoints = reviewPoints;
        this.reviewCount = reviewCount;
        this.explanation = explanation;
        this.recalculatedAt = Instant.now();
    }

    public Company getCompany() {
        return company;
    }

    public BigDecimal getScore() {
        return score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public BigDecimal getInspectionPoints() {
        return inspectionPoints;
    }

    public BigDecimal getLabPoints() {
        return labPoints;
    }

    public BigDecimal getRecallPoints() {
        return recallPoints;
    }

    public BigDecimal getReviewPoints() {
        return reviewPoints;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public String getExplanation() {
        return explanation;
    }

    public Instant getRecalculatedAt() {
        return recalculatedAt;
    }
}
