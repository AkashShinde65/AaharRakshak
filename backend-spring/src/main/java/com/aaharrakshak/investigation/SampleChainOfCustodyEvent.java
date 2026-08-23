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
@Table(name = "sample_chain_of_custody")
public class SampleChainOfCustodyEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @Column(nullable = false, length = 60)
    @Enumerated(EnumType.STRING)
    private SampleChainEventType eventType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_user_id")
    private User actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @Column(length = 220)
    private String locationText;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Instant eventAt = Instant.now();

    protected SampleChainOfCustodyEvent() {
    }

    public SampleChainOfCustodyEvent(
            Sample sample,
            SampleChainEventType eventType,
            User actor,
            User fromUser,
            User toUser,
            String locationText,
            String notes) {
        this.sample = sample;
        this.eventType = eventType;
        this.actor = actor;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.locationText = locationText;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public SampleChainEventType getEventType() {
        return eventType;
    }

    public String getLocationText() {
        return locationText;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getEventAt() {
        return eventAt;
    }
}
