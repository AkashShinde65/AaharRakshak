package com.aaharrakshak.investigation;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "inspection_visits")
public class InspectionVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspector_user_id")
    private User inspector;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scheduled_by_user_id")
    private User scheduledBy;

    @Column(nullable = false)
    private Instant scheduledAt;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private InspectionVisitStatus status = InspectionVisitStatus.SCHEDULED;

    private Instant checkInAt;

    @Column(precision = 10, scale = 7)
    private BigDecimal checkInLatitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal checkInLongitude;

    @Column(length = 220)
    private String locationText;

    @Column(length = 1500)
    private String visitNotes;

    private Instant completedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected InspectionVisit() {
    }

    public InspectionVisit(Complaint complaint, User inspector, User scheduledBy, Instant scheduledAt, String locationText) {
        this.complaint = complaint;
        this.inspector = inspector;
        this.scheduledBy = scheduledBy;
        this.scheduledAt = scheduledAt;
        this.locationText = locationText;
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

    public User getInspector() {
        return inspector;
    }

    public User getScheduledBy() {
        return scheduledBy;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public InspectionVisitStatus getStatus() {
        return status;
    }

    public Instant getCheckInAt() {
        return checkInAt;
    }

    public BigDecimal getCheckInLatitude() {
        return checkInLatitude;
    }

    public BigDecimal getCheckInLongitude() {
        return checkInLongitude;
    }

    public String getLocationText() {
        return locationText;
    }

    public String getVisitNotes() {
        return visitNotes;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void checkIn(BigDecimal latitude, BigDecimal longitude, String locationText) {
        this.status = InspectionVisitStatus.CHECKED_IN;
        this.checkInAt = Instant.now();
        this.checkInLatitude = latitude;
        this.checkInLongitude = longitude;
        this.locationText = locationText;
    }

    public void complete(Instant completedAt, String visitNotes) {
        this.status = InspectionVisitStatus.COMPLETED;
        this.completedAt = completedAt == null ? Instant.now() : completedAt;
        this.visitNotes = visitNotes;
    }
}
