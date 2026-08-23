package com.aaharrakshak.transparency.dto;

import com.aaharrakshak.investigation.ActionType;
import java.time.Instant;
import java.time.LocalDate;

public record PublicAdministrativeActionResponse(
        String actionNumber,
        ActionType actionType,
        LocalDate effectiveDate,
        Boolean simulated,
        String publicSummary,
        Instant decidedAt) {
}
