package com.aaharrakshak.action;

import com.aaharrakshak.storage.StoredFileMetadata;
import com.aaharrakshak.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "company_notice_responses")
public class CompanyNoticeResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notice_id")
    private ShowCauseNotice notice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submitted_by_user_id")
    private User submittedBy;

    @Column(nullable = false, length = 3000)
    private String responseText;

    @Column(nullable = false, length = 500)
    private String objectKey;

    @Column(length = 180)
    private String originalFileName;

    @Column(length = 120)
    private String contentType;

    private Long fileSizeBytes;

    @Column(nullable = false, length = 64)
    private String checksumSha256;

    @Column(length = 600)
    private String storageUri;

    @Column(nullable = false, updatable = false)
    private Instant submittedAt = Instant.now();

    protected CompanyNoticeResponse() {
    }

    public CompanyNoticeResponse(
            ShowCauseNotice notice,
            User submittedBy,
            String responseText,
            StoredFileMetadata storedFile,
            String checksumSha256) {
        this.notice = notice;
        this.submittedBy = submittedBy;
        this.responseText = responseText;
        this.objectKey = storedFile.objectKey();
        this.originalFileName = storedFile.originalFileName();
        this.contentType = storedFile.contentType();
        this.fileSizeBytes = storedFile.sizeBytes();
        this.storageUri = storedFile.storageUri();
        this.checksumSha256 = checksumSha256;
    }

    public Long getId() {
        return id;
    }

    public ShowCauseNotice getNotice() {
        return notice;
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public String getResponseText() {
        return responseText;
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

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
