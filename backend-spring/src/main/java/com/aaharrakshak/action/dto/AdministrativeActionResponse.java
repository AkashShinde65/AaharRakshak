package com.aaharrakshak.action.dto;

import com.aaharrakshak.investigation.ActionType;
import java.time.Instant;
import java.time.LocalDate;

public record AdministrativeActionResponse(
        Long actionId,
        String actionNumber,
        String ticketNumber,
        String reportNumber,
        String companyName,
        ActionType actionType,
        String reason,
        String evidenceSummary,
        LocalDate effectiveDate,
        Long approvingOfficialId,
        String approvingOfficialName,
        Boolean simulated,
        String publicSummary,
        Instant decidedAt) {
}
