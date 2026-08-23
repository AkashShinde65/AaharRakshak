package com.aaharrakshak.intelligence;

import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.complaint.ComplaintStatus;
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

@Entity
@Table(name = "sla_escalations")
public class SlaEscalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_inspector_user_id")
    private User assignedInspector;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "escalated_to_user_id")
    private User escalatedTo;

    @Column(nullable = false, length = 60)
    @Enumerated(EnumType.STRING)
    private ComplaintStatus previousStatus;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(nullable = false, updatable = false)
    private Instant escalatedAt = Instant.now();

    private Instant acknowledgedAt;

    protected SlaEscalation() {
    }

    public SlaEscalation(Complaint complaint, User assignedInspector, User escalatedTo, ComplaintStatus previousStatus, String reason) {
        this.complaint = complaint;
        this.assignedInspector = assignedInspector;
        this.escalatedTo = escalatedTo;
        this.previousStatus = previousStatus;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public User getAssignedInspector() {
        return assignedInspector;
    }

    public User getEscalatedTo() {
        return escalatedTo;
    }

    public ComplaintStatus getPreviousStatus() {
        return previousStatus;
    }

    public String getReason() {
        return reason;
    }

    public Instant getEscalatedAt() {
        return escalatedAt;
    }
}
