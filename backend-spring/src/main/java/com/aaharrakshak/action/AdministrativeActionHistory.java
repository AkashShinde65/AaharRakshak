package com.aaharrakshak.action;

import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.investigation.Action;
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
@Table(name = "administrative_action_history")
public class AdministrativeActionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private ShowCauseNotice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id")
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id")
    private User actor;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AdministrativeActionHistory() {
    }

    public AdministrativeActionHistory(
            Complaint complaint,
            ShowCauseNotice notice,
            Action action,
            User actor,
            String eventType,
            String notes) {
        this.complaint = complaint;
        this.notice = notice;
        this.action = action;
        this.actor = actor;
        this.eventType = eventType;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public ShowCauseNotice getNotice() {
        return notice;
    }

    public Action getAction() {
        return action;
    }

    public User getActor() {
        return actor;
    }

    public String getEventType() {
        return eventType;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
