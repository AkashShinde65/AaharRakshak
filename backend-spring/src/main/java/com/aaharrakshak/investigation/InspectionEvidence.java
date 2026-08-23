package com.aaharrakshak.investigation;

import com.aaharrakshak.complaint.EvidenceType;
import com.aaharrakshak.storage.StoredFileMetadata;
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
@Table(name = "inspection_evidence")
public class InspectionEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspection_visit_id")
    private InspectionVisit inspectionVisit;

    @Column(nullable = false, length = 60)
    @Enumerated(EnumType.STRING)
    private EvidenceType type;

    @Column(nullable = false, length = 500)
    private String objectKey;

    @Column(length = 180)
    private String originalFileName;

    @Column(length = 120)
    private String contentType;

    private Long fileSizeBytes;

    @Column(nullable = false, length = 64)
    private String checksumSha256;

    private Instant capturedAt;

    @Column(nullable = false)
    private Instant uploadedAt = Instant.now();

    @Column(length = 600)
    private String storageUri;

    protected InspectionEvidence() {
    }

    public InspectionEvidence(
            InspectionVisit inspectionVisit,
            EvidenceType type,
            StoredFileMetadata storedFile,
            String checksumSha256,
            Instant capturedAt) {
        this.inspectionVisit = inspectionVisit;
        this.type = type;
        this.objectKey = storedFile.objectKey();
        this.originalFileName = storedFile.originalFileName();
        this.contentType = storedFile.contentType();
        this.fileSizeBytes = storedFile.sizeBytes();
        this.storageUri = storedFile.storageUri();
        this.checksumSha256 = checksumSha256;
        this.capturedAt = capturedAt;
    }

    public Long getId() {
        return id;
    }

    public EvidenceType getType() {
        return type;
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

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public String getStorageUri() {
        return storageUri;
    }
}
