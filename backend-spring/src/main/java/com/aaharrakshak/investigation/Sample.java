package com.aaharrakshak.investigation;

import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "samples")
public class Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_visit_id")
    private InspectionVisit inspectionVisit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by_user_id")
    private User collectedBy;

    @Column(length = 80, unique = true)
    private String sampleNumber;

    @Column(nullable = false, unique = true, length = 80)
    private String sealNumber;

    @Column(length = 80)
    private String quantity;

    @Column(length = 1000)
    private String chainOfCustody;

    @Column(nullable = false)
    private Instant collectedAt;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 220)
    private String locationText;

    @Column(length = 500)
    private String storageDetails;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected Sample() {
    }

    public Sample(
            Complaint complaint,
            InspectionVisit inspectionVisit,
            User collectedBy,
            String sampleNumber,
            String sealNumber,
            String quantity,
            String chainOfCustody,
            Instant collectedAt,
            BigDecimal latitude,
            BigDecimal longitude,
            String locationText,
            String storageDetails) {
        this.complaint = complaint;
        this.inspectionVisit = inspectionVisit;
        this.collectedBy = collectedBy;
        this.sampleNumber = sampleNumber;
        this.sealNumber = sealNumber;
        this.quantity = quantity;
        this.chainOfCustody = chainOfCustody;
        this.collectedAt = collectedAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationText = locationText;
        this.storageDetails = storageDetails;
    }

    @PreUpdate
    void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public InspectionVisit getInspectionVisit() {
        return inspectionVisit;
    }

    public User getCollectedBy() {
        return collectedBy;
    }

    public String getSampleNumber() {
        return sampleNumber;
    }

    public String getSealNumber() {
        return sealNumber;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getChainOfCustody() {
        return chainOfCustody;
    }

    public Instant getCollectedAt() {
        return collectedAt;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getLocationText() {
        return locationText;
    }

    public String getStorageDetails() {
        return storageDetails;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
