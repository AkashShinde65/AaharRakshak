package com.aaharrakshak.intelligence.dto;

import com.aaharrakshak.intelligence.ExternalEventStatus;
import java.time.Instant;

public record MockExternalEventResponse(
        Long eventId,
        String eventType,
        String targetType,
        String targetId,
        ExternalEventStatus status,
        Instant createdAt,
        String safetyNote) {
}
