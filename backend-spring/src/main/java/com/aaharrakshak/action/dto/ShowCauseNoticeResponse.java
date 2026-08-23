package com.aaharrakshak.action.dto;

import com.aaharrakshak.action.AdministrativeNoticeStatus;
import com.aaharrakshak.investigation.LabOutcome;
import java.time.Instant;
import java.util.List;

public record ShowCauseNoticeResponse(
        Long noticeId,
        String noticeNumber,
        String ticketNumber,
        String reportNumber,
        LabOutcome outcome,
        Long companyId,
        String companyName,
        String productName,
        String batchNumber,
        String subject,
        String reason,
        String evidenceSummary,
        Instant responseDueAt,
        AdministrativeNoticeStatus status,
        Instant issuedAt,
        List<CompanyNoticeResponseResponse> responses,
        AdministrativeActionResponse action,
        List<AdministrativeActionHistoryResponse> history) {
}
