package com.aaharrakshak.intelligence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "complaint_hotspots")
public class ComplaintHotspot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 160)
    private String hotspotKey;

    @Column(nullable = false, length = 120)
    private String district;

    @Column(nullable = false, length = 220)
    private String relatedKey;

    @Column(nullable = false, length = 180)
    private String productOrVendor;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private Integer complaintCount;

    @Column(nullable = false, precision = 8, scale = 3)
    private BigDecimal radiusKm;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal centerLatitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal centerLongitude;

    @Column(nullable = false)
    private Instant windowStart;

    @Column(nullable = false)
    private Instant windowEnd;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant detectedAt = Instant.now();

    protected ComplaintHotspot() {
    }

    public ComplaintHotspot(
            String hotspotKey,
            String district,
            String relatedKey,
            String productOrVendor,
            RiskLevel riskLevel,
            Integer complaintCount,
            BigDecimal radiusKm,
            BigDecimal centerLatitude,
            BigDecimal centerLongitude,
            Instant windowStart,
            Instant windowEnd) {
        this.hotspotKey = hotspotKey;
        this.district = district;
        this.relatedKey = relatedKey;
        this.productOrVendor = productOrVendor;
        this.riskLevel = riskLevel;
        this.complaintCount = complaintCount;
        this.radiusKm = radiusKm;
        this.centerLatitude = centerLatitude;
        this.centerLongitude = centerLongitude;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    public Long getId() {
        return id;
    }

    public String getHotspotKey() {
        return hotspotKey;
    }

    public String getDistrict() {
        return district;
    }

    public String getRelatedKey() {
        return relatedKey;
    }

    public String getProductOrVendor() {
        return productOrVendor;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public Integer getComplaintCount() {
        return complaintCount;
    }

    public BigDecimal getRadiusKm() {
        return radiusKm;
    }

    public BigDecimal getCenterLatitude() {
        return centerLatitude;
    }

    public BigDecimal getCenterLongitude() {
        return centerLongitude;
    }

    public Instant getWindowStart() {
        return windowStart;
    }

    public Instant getWindowEnd() {
        return windowEnd;
    }

    public Boolean getActive() {
        return active;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }
}
