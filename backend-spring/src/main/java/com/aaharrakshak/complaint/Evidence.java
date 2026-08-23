package com.aaharrakshak.complaint;

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
@Table(name = "evidence")
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

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

    @Column(length = 600)
    private String storageUri;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt = Instant.now();

    protected Evidence() {
    }

    public Evidence(
            Complaint complaint,
            EvidenceType type,
            StoredFileMetadata fileMetadata,
            String checksumSha256,
            Instant capturedAt) {
        this.complaint = complaint;
        this.type = type;
        this.objectKey = fileMetadata.objectKey();
        this.originalFileName = fileMetadata.originalFileName();
        this.contentType = fileMetadata.contentType();
        this.fileSizeBytes = fileMetadata.sizeBytes();
        this.storageUri = fileMetadata.storageUri();
        this.checksumSha256 = checksumSha256;
        this.capturedAt = capturedAt;
    }

    public Long getId() {
        return id;
    }

    public Complaint getComplaint() {
        return complaint;
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

    public String getStorageUri() {
        return storageUri;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }
}
