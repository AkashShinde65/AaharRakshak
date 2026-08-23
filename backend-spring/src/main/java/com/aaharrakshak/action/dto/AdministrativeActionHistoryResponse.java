package com.aaharrakshak.action.dto;

import java.time.Instant;

public record AdministrativeActionHistoryResponse(
        String eventType,
        String notes,
        String actorName,
        Instant createdAt) {
}
