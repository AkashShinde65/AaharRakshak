package com.aaharrakshak.intelligence.dto;

import com.aaharrakshak.intelligence.AlertOutboxStatus;
import com.aaharrakshak.intelligence.NotificationChannel;
import java.time.Instant;

public record AlertOutboxResponse(
        Long alertId,
        Long userId,
        String eventType,
        NotificationChannel channel,
        String subject,
        String body,
        AlertOutboxStatus status,
        Integer retryCount,
        Instant createdAt) {
}
