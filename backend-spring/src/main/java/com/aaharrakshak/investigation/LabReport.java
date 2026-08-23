package com.aaharrakshak.investigation;

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

@Entity
@Table(name = "lab_reports")
public class LabReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @Column(nullable = false, length = 80)
    private String reportNumber;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private LabReportStatus status = LabReportStatus.DRAFT;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private LabOutcome outcome = LabOutcome.INCONCLUSIVE;

    @Column(nullable = false, length = 500)
    private String objectKey;

    @Column(length = 180)
    private String originalFileName;

    @Column(length = 120)
    private String contentType;

    private Long fileSizeBytes;

    @Column(length = 64)
    private String checksumSha256;

    @Column(length = 600)
    private String storageUri;

    @Column(length = 80)
    private String resultSummary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_user_id")
    private User submittedBy;

    private Instant submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_user_id")
    private User reviewedBy;

    private Instant reviewedAt;

    private Instant publishedAt;

    @Column(nullable = false)
    private Instant uploadedAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected LabReport() {
    }

    public LabReport(
            Sample sample,
            String reportNumber,
            StoredFileMetadata storedFile,
            String checksumSha256,
            String resultSummary,
            LabOutcome outcome) {
        this.sample = sample;
        this.reportNumber = reportNumber;
        this.objectKey = storedFile.objectKey();
        this.originalFileName = storedFile.originalFileName();
        this.contentType = storedFile.contentType();
        this.fileSizeBytes = storedFile.sizeBytes();
        this.storageUri = storedFile.storageUri();
        this.checksumSha256 = checksumSha256;
        this.resultSummary = resultSummary;
        this.outcome = outcome == null ? LabOutcome.INCONCLUSIVE : outcome;
        this.status = LabReportStatus.DRAFT;
    }

    @PreUpdate
    void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Sample getSample() {
        return sample;
    }

    public String getReportNumber() {
        return reportNumber;
    }

    public LabReportStatus getStatus() {
        return status;
    }

    public LabOutcome getOutcome() {
        return outcome;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public String getChecksumSha256() {
        return checksumSha256;
    }

    public String getStorageUri() {
        return storageUri;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void submit(User labOfficer) {
        this.status = LabReportStatus.SUBMITTED;
        this.submittedBy = labOfficer;
        this.submittedAt = Instant.now();
    }

    public void review(User reviewer) {
        this.status = LabReportStatus.REVIEWED;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
    }

    public void publish() {
        this.status = LabReportStatus.PUBLISHED;
        this.publishedAt = Instant.now();
    }
}
