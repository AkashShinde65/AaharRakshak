package com.aaharrakshak.action;

import com.aaharrakshak.company.Company;
import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.investigation.LabReport;
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
@Table(name = "show_cause_notices")
public class ShowCauseNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String noticeNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lab_report_id")
    private LabReport labReport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issued_by_user_id")
    private User issuedBy;

    @Column(nullable = false, length = 180)
    private String subject;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(nullable = false, length = 1200)
    private String evidenceSummary;

    @Column(nullable = false)
    private Instant responseDueAt;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private AdministrativeNoticeStatus status = AdministrativeNoticeStatus.ISSUED;

    @Column(nullable = false, updatable = false)
    private Instant issuedAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected ShowCauseNotice() {
    }

    public ShowCauseNotice(
            String noticeNumber,
            Complaint complaint,
            LabReport labReport,
            Company company,
            User issuedBy,
            String subject,
            String reason,
            String evidenceSummary,
            Instant responseDueAt) {
        this.noticeNumber = noticeNumber;
        this.complaint = complaint;
        this.labReport = labReport;
        this.company = company;
        this.issuedBy = issuedBy;
        this.subject = subject;
        this.reason = reason;
        this.evidenceSummary = evidenceSummary;
        this.responseDueAt = responseDueAt;
    }

    @PreUpdate
    void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getNoticeNumber() {
        return noticeNumber;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public LabReport getLabReport() {
        return labReport;
    }

    public Company getCompany() {
        return company;
    }

    public User getIssuedBy() {
        return issuedBy;
    }

    public String getSubject() {
        return subject;
    }

    public String getReason() {
        return reason;
    }

    public String getEvidenceSummary() {
        return evidenceSummary;
    }

    public Instant getResponseDueAt() {
        return responseDueAt;
    }

    public AdministrativeNoticeStatus getStatus() {
        return status;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void markResponded() {
        this.status = AdministrativeNoticeStatus.RESPONDED;
    }

    public void markUnderReview() {
        this.status = AdministrativeNoticeStatus.UNDER_REVIEW;
    }

    public void markDecided() {
        this.status = AdministrativeNoticeStatus.DECIDED;
    }
}
