package com.aaharrakshak.intelligence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReceiptMetadataRequest(
        @NotBlank @Size(max = 500) String objectKey,
        @NotBlank @Size(max = 180) String originalFileName,
        @NotBlank @Size(max = 120) String contentType,
        @Positive long sizeBytes,
        @NotBlank @Size(min = 64, max = 64) String checksumSha256) {
}
