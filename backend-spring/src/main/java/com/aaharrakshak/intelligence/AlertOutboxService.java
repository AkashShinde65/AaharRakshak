package com.aaharrakshak.intelligence;

import com.aaharrakshak.catalog.Batch;
import com.aaharrakshak.catalog.Product;
import com.aaharrakshak.company.Company;
import com.aaharrakshak.intelligence.dto.AlertOutboxResponse;
import com.aaharrakshak.notification.Notification;
import com.aaharrakshak.notification.NotificationRepository;
import com.aaharrakshak.notification.NotificationStatus;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlertOutboxService {

    private final AlertOutboxRepository alertOutboxRepository;
    private final NotificationRepository notificationRepository;
    private final List<NotificationChannelAdapter> channelAdapters;
    private final AlertEventPublisher eventPublisher;
    private final AlertWebSocketSessionRegistry webSocketSessionRegistry;
    private final ObjectMapper objectMapper;

    public AlertOutboxService(
            AlertOutboxRepository alertOutboxRepository,
            NotificationRepository notificationRepository,
            List<NotificationChannelAdapter> channelAdapters,
            AlertEventPublisher eventPublisher,
            AlertWebSocketSessionRegistry webSocketSessionRegistry,
            ObjectMapper objectMapper) {
        this.alertOutboxRepository = alertOutboxRepository;
        this.notificationRepository = notificationRepository;
        this.channelAdapters = channelAdapters;
        this.eventPublisher = eventPublisher;
        this.webSocketSessionRegistry = webSocketSessionRegistry;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void enqueue(
            User user,
            String eventType,
            String subject,
            String body,
            Map<String, Object> payload,
            String locationText,
            Company company,
            Product product,
            Batch batch,
            List<NotificationChannel> channels) {
        channels.forEach(channel -> {
            AlertOutbox alert = alertOutboxRepository.save(new AlertOutbox(
                    user,
                    eventType,
                    channel,
                    subject,
                    body,
                    toJson(payload),
                    locationText,
                    company,
                    product,
                    batch));
            if (channel == NotificationChannel.IN_APP && user != null) {
                notificationRepository.save(new Notification(user, "IN_APP", subject, body, NotificationStatus.SENT));
            }
            dispatch(alert);
        });
    }

    @Transactional
    public int retryDueAlerts() {
        List<AlertOutbox> due = alertOutboxRepository.findByStatusInAndNextAttemptAtBeforeOrderByCreatedAtAsc(
                List.of(AlertOutboxStatus.PENDING, AlertOutboxStatus.FAILED),
                Instant.now());
        due.forEach(this::dispatch);
        return due.size();
    }

    @Scheduled(
            initialDelayString = "${app.intelligence.alert-retry-initial-delay-ms:60000}",
            fixedDelayString = "${app.intelligence.alert-retry-delay-ms:60000}")
    @Transactional
    public void scheduledRetry() {
        retryDueAlerts();
    }

    @Transactional(readOnly = true)
    public List<AlertOutboxResponse> myAlerts(AuthenticatedUser principal) {
        return alertOutboxRepository.findByUserIdOrderByCreatedAtDesc(principal.getUserId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlertOutboxResponse> allAlerts(AuthenticatedUser principal) {
        requireSeniorOfficial(principal);
        return alertOutboxRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    private void dispatch(AlertOutbox alert) {
        try {
            channelAdapters.stream()
                    .filter(adapter -> adapter.supports(alert.getChannel()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No notification adapter for " + alert.getChannel()))
                    .send(alert);
            alert.markSent();
            eventPublisher.publish(alert);
            if (alert.getUser() != null) {
                webSocketSessionRegistry.send(alert.getUser().getId(), toJson(Map.of(
                        "alertId", alert.getId(),
                        "eventType", alert.getEventType(),
                        "channel", alert.getChannel().name(),
                        "subject", alert.getSubject(),
                        "body", alert.getBody())));
            }
        } catch (RuntimeException ex) {
            alert.markFailed(ex.getMessage());
        }
    }

    private AlertOutboxResponse toResponse(AlertOutbox alert) {
        return new AlertOutboxResponse(
                alert.getId(),
                alert.getUser() == null ? null : alert.getUser().getId(),
                alert.getEventType(),
                alert.getChannel(),
                alert.getSubject(),
                alert.getBody(),
                alert.getStatus(),
                alert.getRetryCount(),
                alert.getCreatedAt());
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Could not serialize alert payload", ex);
        }
    }

    private void requireSeniorOfficial(AuthenticatedUser principal) {
        if (principal.getRoles().contains(RoleName.DISTRICT_ESCALATION_OFFICER)
                || principal.getRoles().contains(RoleName.CENTRAL_ADMINISTRATOR)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Senior official role required");
    }
}
