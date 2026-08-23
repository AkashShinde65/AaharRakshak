package com.aaharrakshak.transparency.dto;

import com.aaharrakshak.investigation.LabOutcome;
import java.time.Instant;
import java.util.List;

public record PublicLabReportResponse(
        String reportNumber,
        String ticketNumber,
        LabOutcome outcome,
        String resultSummary,
        Instant publishedAt,
        String companyName,
        String productName,
        String batchNumber,
        String district,
        List<PublicLabResultResponse> results,
        PublicAdministrativeActionResponse action,
        String privacyNotice) {
}
