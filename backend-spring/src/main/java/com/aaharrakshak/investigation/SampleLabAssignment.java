package com.aaharrakshak.investigation;

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
@Table(name = "sample_lab_assignments")
public class SampleLabAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_by_user_id")
    private User assignedBy;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private SampleLabAssignmentStatus status = SampleLabAssignmentStatus.ASSIGNED;

    @Column(nullable = false)
    private Instant assignedAt = Instant.now();

    private Instant receivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by_user_id")
    private User receivedBy;

    @Column(length = 500)
    private String notes;

    protected SampleLabAssignment() {
    }

    public SampleLabAssignment(Sample sample, User assignedTo, User assignedBy, String notes) {
        this.sample = sample;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public Sample getSample() {
        return sample;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public SampleLabAssignmentStatus getStatus() {
        return status;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void markReceived(User receivedBy) {
        this.status = SampleLabAssignmentStatus.RECEIVED;
        this.receivedBy = receivedBy;
        this.receivedAt = Instant.now();
    }
}
