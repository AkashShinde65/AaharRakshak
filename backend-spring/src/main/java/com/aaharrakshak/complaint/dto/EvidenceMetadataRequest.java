package com.aaharrakshak.complaint.dto;

import com.aaharrakshak.complaint.EvidenceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record EvidenceMetadataRequest(
        @NotNull EvidenceType type,
        @NotBlank @Size(max = 500) String objectKey,
        @Size(max = 180) String originalFileName,
        @NotBlank @Size(max = 120) String contentType,
        @NotNull @Min(1) Long sizeBytes,
        @NotBlank @Pattern(regexp = "^[a-fA-F0-9]{64}$", message = "checksumSha256 must be a 64-character SHA-256 hex digest")
        String checksumSha256,
        @NotNull Instant capturedAt) {
}
