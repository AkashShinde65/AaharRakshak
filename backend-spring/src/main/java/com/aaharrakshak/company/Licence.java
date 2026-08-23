package com.aaharrakshak.company;

import com.aaharrakshak.storage.StoredFileMetadata;
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
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "licences")
public class Licence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false, unique = true, length = 40)
    private String licenceNumber;

    @Column(length = 120)
    private String issuingAuthority;

    private LocalDate validFrom;

    private LocalDate validTo;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private LicenceStatus status = LicenceStatus.PENDING_VERIFICATION;

    @Column(length = 500)
    private String labelImageObjectKey;

    @Column(length = 180)
    private String labelImageFileName;

    @Column(length = 120)
    private String labelImageContentType;

    private Long labelImageSizeBytes;

    @Column(length = 40)
    private String registryStatus;

    @Column(length = 120)
    private String registryReferenceToken;

    @Column(length = 500)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_user_id")
    private User reviewedByUser;

    private Instant reviewedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected Licence() {
    }

    public Licence(
            Company company,
            String licenceNumber,
            String issuingAuthority,
            LocalDate validFrom,
            LocalDate validTo,
            StoredFileMetadata labelImage) {
        this.company = company;
        this.licenceNumber = licenceNumber;
        this.issuingAuthority = issuingAuthority;
        this.validFrom = validFrom;
        this.validTo = validTo;
        applyLabelImage(labelImage);
    }

    @PreUpdate
    void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public LicenceStatus getStatus() {
        return status;
    }

    public String getLabelImageObjectKey() {
        return labelImageObjectKey;
    }

    public String getLabelImageFileName() {
        return labelImageFileName;
    }

    public String getLabelImageContentType() {
        return labelImageContentType;
    }

    public Long getLabelImageSizeBytes() {
        return labelImageSizeBytes;
    }

    public String getRegistryStatus() {
        return registryStatus;
    }

    public String getRegistryReferenceToken() {
        return registryReferenceToken;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void verify(User reviewer, RegistryLicenceDetails registryDetails) {
        this.issuingAuthority = registryDetails.issuingAuthority();
        this.validFrom = registryDetails.validFrom();
        this.validTo = registryDetails.validTo();
        this.registryStatus = registryDetails.status();
        this.registryReferenceToken = registryDetails.referenceToken();
        this.rejectionReason = null;
        this.reviewedByUser = reviewer;
        this.reviewedAt = Instant.now();
        this.status = registryDetails.validTo().isBefore(LocalDate.now())
                ? LicenceStatus.EXPIRED
                : LicenceStatus.ACTIVE;
    }

    public void reject(User reviewer, String reason, String registryStatus, String registryReferenceToken) {
        this.status = LicenceStatus.REJECTED;
        this.rejectionReason = reason;
        this.registryStatus = registryStatus;
        this.registryReferenceToken = registryReferenceToken;
        this.reviewedByUser = reviewer;
        this.reviewedAt = Instant.now();
    }

    public void expire(User reviewer) {
        this.status = LicenceStatus.EXPIRED;
        this.reviewedByUser = reviewer;
        this.reviewedAt = Instant.now();
    }

    private void applyLabelImage(StoredFileMetadata labelImage) {
        if (labelImage == null) {
            return;
        }
        this.labelImageObjectKey = labelImage.objectKey();
        this.labelImageFileName = labelImage.originalFileName();
        this.labelImageContentType = labelImage.contentType();
        this.labelImageSizeBytes = labelImage.sizeBytes();
    }
}
