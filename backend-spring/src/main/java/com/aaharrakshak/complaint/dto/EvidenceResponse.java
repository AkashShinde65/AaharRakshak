package com.aaharrakshak.complaint.dto;

import com.aaharrakshak.complaint.EvidenceType;
import java.time.Instant;

public record EvidenceResponse(
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
