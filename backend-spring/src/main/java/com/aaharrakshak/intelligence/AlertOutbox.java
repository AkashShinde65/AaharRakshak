package com.aaharrakshak.intelligence;

import com.aaharrakshak.catalog.Batch;
import com.aaharrakshak.catalog.Product;
import com.aaharrakshak.company.Company;
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
@Table(name = "alert_outbox")
public class AlertOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Column(nullable = false, length = 180)
    private String subject;

    @Column(nullable = false, length = 1200)
    private String body;

    @Column(length = 3000)
    private String payloadJson;

    @Column(length = 220)
    private String locationText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private AlertOutboxStatus status = AlertOutboxStatus.PENDING;

    @Column(nullable = false)
    private Integer retryCount = 0;

    @Column(nullable = false)
    private Instant nextAttemptAt = Instant.now();

    private Instant sentAt;

    @Column(length = 500)
    private String lastError;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AlertOutbox() {
    }

    public AlertOutbox(
            User user,
            String eventType,
            NotificationChannel channel,
            String subject,
            String body,
            String payloadJson,
            String locationText,
            Company company,
            Product product,
            Batch batch) {
        this.user = user;
        this.eventType = eventType;
        this.channel = channel;
        this.subject = subject;
        this.body = body;
        this.payloadJson = payloadJson;
        this.locationText = locationText;
        this.company = company;
        this.product = product;
        this.batch = batch;
    }

    public void markSent() {
        this.status = AlertOutboxStatus.SENT;
        this.sentAt = Instant.now();
        this.lastError = null;
    }

    public void markFailed(String error) {
        this.status = AlertOutboxStatus.FAILED;
        this.retryCount += 1;
        this.nextAttemptAt = Instant.now().plusSeconds(Math.min(3600, retryCount * 60L));
        this.lastError = error;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getEventType() {
        return eventType;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public String getLocationText() {
        return locationText;
    }

    public AlertOutboxStatus getStatus() {
        return status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
