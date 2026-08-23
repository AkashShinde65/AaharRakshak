package com.aaharrakshak.intelligence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "mock_external_events")
public class MockExternalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false, length = 80)
    private String targetType;

    @Column(nullable = false, length = 120)
    private String targetId;

    @Column(nullable = false, length = 3000)
    private String payloadJson;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private ExternalEventStatus status = ExternalEventStatus.MOCK_PUBLISHED;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected MockExternalEvent() {
    }

    public MockExternalEvent(String eventType, String targetType, String targetId, String payloadJson) {
        this.eventType = eventType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.payloadJson = payloadJson;
    }

    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public ExternalEventStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
