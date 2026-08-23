package com.aaharrakshak.investigation;

import com.aaharrakshak.action.ShowCauseNotice;
import com.aaharrakshak.company.Company;
import com.aaharrakshak.complaint.Complaint;
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
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "actions")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_report_id")
    private LabReport labReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private ShowCauseNotice notice;

    @Column(unique = true, length = 80)
    private String actionNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decided_by_user_id")
    private User decidedBy;

    @Column(nullable = false, length = 60)
    @Enumerated(EnumType.STRING)
    private ActionType type;

    @Column(nullable = false, length = 1000)
    private String summary;

    @Column(length = 1000)
    private String reason;

    @Column(length = 1200)
    private String evidenceSummary;

    private LocalDate effectiveDate;

    @Column(nullable = false)
    private Boolean simulated = true;

    @Column(length = 1000)
    private String publicSummary;

    @Column(nullable = false)
    private Instant decidedAt = Instant.now();

    protected Action() {
    }

    public Action(
            Complaint complaint,
            LabReport labReport,
            Company company,
            ShowCauseNotice notice,
            String actionNumber,
            User decidedBy,
            ActionType type,
            String reason,
            String evidenceSummary,
            LocalDate effectiveDate,
            String publicSummary) {
        this.complaint = complaint;
        this.labReport = labReport;
        this.company = company;
        this.notice = notice;
        this.actionNumber = actionNumber;
        this.decidedBy = decidedBy;
        this.type = type;
        this.reason = reason;
        this.summary = reason;
        this.evidenceSummary = evidenceSummary;
        this.effectiveDate = effectiveDate;
        this.publicSummary = publicSummary;
        this.simulated = true;
        this.decidedAt = Instant.now();
    }

    public Long getId() {
        return id;
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

    public ShowCauseNotice getNotice() {
        return notice;
    }

    public String getActionNumber() {
        return actionNumber;
    }

    public User getDecidedBy() {
        return decidedBy;
    }

    public ActionType getType() {
        return type;
    }

    public String getSummary() {
        return summary;
    }

    public String getReason() {
        return reason;
    }

    public String getEvidenceSummary() {
        return evidenceSummary;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public Boolean getSimulated() {
        return simulated;
    }

    public String getPublicSummary() {
        return publicSummary;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }
}
