package com.aaharrakshak.investigation.dto;

import com.aaharrakshak.investigation.LabOutcome;
import com.aaharrakshak.investigation.LabReportStatus;
import java.time.Instant;
import java.util.List;

public record LabReportResponse(
        Long reportId,
        Long sampleId,
        String sampleNumber,
        String ticketNumber,
        String reportNumber,
        LabReportStatus status,
        LabOutcome outcome,
        String objectKey,
        String originalFileName,
        String contentType,
        Long sizeBytes,
        String checksumSha256,
        String resultSummary,
        Instant uploadedAt,
        Instant submittedAt,
        Instant reviewedAt,
        Instant publishedAt,
        List<LabTestResultResponse> results) {
}
