package com.aaharrakshak.complaint;

import com.aaharrakshak.catalog.Batch;
import com.aaharrakshak.catalog.Product;
import com.aaharrakshak.company.Company;
import com.aaharrakshak.user.User;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String ticketNumber;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private ComplaintType complaintType = ComplaintType.PACKAGED_FOOD;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "citizen_id")
    private User citizen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(nullable = false, length = 60)
    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;

    @Column(nullable = false, length = 60)
    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.DRAFT;

    @Column(length = 1000)
    private String description;

    @Column(length = 32)
    private String scannedBarcode;

    @Column(length = 180)
    private String detectedProductName;

    @Column(length = 180)
    private String detectedCompanyName;

    @Column(length = 14)
    private String detectedFssaiLicenceNumber;

    @Column(length = 80)
    private String detectedBatchNumber;

    private LocalDate detectedExpiryDate;

    @Column(length = 180)
    private String confirmedProductName;

    @Column(length = 180)
    private String confirmedCompanyName;

    @Column(length = 14)
    private String confirmedFssaiLicenceNumber;

    @Column(length = 80)
    private String confirmedBatchNumber;

    private LocalDate confirmedExpiryDate;

    @Column(length = 180)
    private String vendorName;

    @Column(length = 300)
    private String vendorAddress;

    @Column(nullable = false)
    private Boolean gpsConsent = false;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 220)
    private String locationText;

    @Column(length = 120)
    private String district;

    private Instant slaDueAt;

    @Column(nullable = false)
    private Integer riskScore = 0;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    private Instant submittedAt;

    protected Complaint() {
    }

    public Complaint(User citizen, ComplaintType complaintType, ComplaintCategory category, String ticketNumber) {
        this.citizen = citizen;
        this.complaintType = complaintType;
        this.category = category;
        this.ticketNumber = ticketNumber;
        this.status = ComplaintStatus.DRAFT;
    }

    @PreUpdate
    void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public ComplaintType getComplaintType() {
        return complaintType;
    }

    public User getCitizen() {
        return citizen;
    }

    public Company getCompany() {
        return company;
    }

    public Product getProduct() {
        return product;
    }

    public Batch getBatch() {
        return batch;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getScannedBarcode() {
        return scannedBarcode;
    }

    public String getDetectedProductName() {
        return detectedProductName;
    }

    public String getDetectedCompanyName() {
        return detectedCompanyName;
    }

    public String getDetectedFssaiLicenceNumber() {
        return detectedFssaiLicenceNumber;
    }

    public String getDetectedBatchNumber() {
        return detectedBatchNumber;
    }

    public LocalDate getDetectedExpiryDate() {
        return detectedExpiryDate;
    }

    public String getConfirmedProductName() {
        return confirmedProductName;
    }

    public String getConfirmedCompanyName() {
        return confirmedCompanyName;
    }

    public String getConfirmedFssaiLicenceNumber() {
        return confirmedFssaiLicenceNumber;
    }

    public String getConfirmedBatchNumber() {
        return confirmedBatchNumber;
    }

    public LocalDate getConfirmedExpiryDate() {
        return confirmedExpiryDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public Boolean getGpsConsent() {
        return gpsConsent;
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

    public String getDistrict() {
        return district;
    }

    public Instant getSlaDueAt() {
        return slaDueAt;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void linkCatalogue(Product product, Batch batch) {
        this.product = product;
        this.batch = batch;
        this.company = product == null ? null : product.getCompany();
    }

    public void applyDraftDetails(
            String scannedBarcode,
            String detectedProductName,
            String detectedCompanyName,
            String detectedFssaiLicenceNumber,
            String detectedBatchNumber,
            LocalDate detectedExpiryDate,
            String confirmedProductName,
            String confirmedCompanyName,
            String confirmedFssaiLicenceNumber,
            String confirmedBatchNumber,
            LocalDate confirmedExpiryDate,
            String vendorName,
            String vendorAddress,
            String description,
            Boolean gpsConsent,
            BigDecimal latitude,
            BigDecimal longitude,
            String locationText) {
        this.scannedBarcode = scannedBarcode;
        this.detectedProductName = detectedProductName;
        this.detectedCompanyName = detectedCompanyName;
        this.detectedFssaiLicenceNumber = detectedFssaiLicenceNumber;
        this.detectedBatchNumber = detectedBatchNumber;
        this.detectedExpiryDate = detectedExpiryDate;
        this.confirmedProductName = confirmedProductName;
        this.confirmedCompanyName = confirmedCompanyName;
        this.confirmedFssaiLicenceNumber = confirmedFssaiLicenceNumber;
        this.confirmedBatchNumber = confirmedBatchNumber;
        this.confirmedExpiryDate = confirmedExpiryDate;
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
        this.description = description;
        this.gpsConsent = gpsConsent != null && gpsConsent;
        this.latitude = this.gpsConsent ? latitude : null;
        this.longitude = this.gpsConsent ? longitude : null;
        this.locationText = this.gpsConsent ? locationText : null;
    }

    public void submit() {
        this.status = ComplaintStatus.SUBMITTED;
        this.submittedAt = Instant.now();
    }

    public void assign() {
        this.status = ComplaintStatus.ASSIGNED;
    }

    public void applyInvestigationAssignment(String district, Instant slaDueAt) {
        this.district = district;
        this.slaDueAt = slaDueAt;
    }

    public void changeStatus(ComplaintStatus status) {
        this.status = status;
    }
}
