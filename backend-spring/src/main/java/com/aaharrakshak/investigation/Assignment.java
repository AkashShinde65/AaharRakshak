package com.aaharrakshak.investigation;

import com.aaharrakshak.complaint.Complaint;
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
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_by_user_id")
    private User assignedBy;

    @Column(nullable = false)
    private Instant assignedAt = Instant.now();

    @Column(length = 500)
    private String notes;

    protected Assignment() {
    }

    public Assignment(Complaint complaint, User assignedTo, User assignedBy, String notes) {
        this.complaint = complaint;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public String getNotes() {
        return notes;
    }
}
