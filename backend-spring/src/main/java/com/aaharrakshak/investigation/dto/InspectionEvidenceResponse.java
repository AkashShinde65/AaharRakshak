package com.aaharrakshak.investigation.dto;

import com.aaharrakshak.complaint.EvidenceType;
import java.time.Instant;

public record InspectionEvidenceResponse(
        Long evidenceId,
        EvidenceType type,
        String objectKey,
        String originalFileName,
        String contentType,
        Long sizeBytes,
        String checksumSha256,
        Instant capturedAt,
        Instant uploadedAt) {
}
