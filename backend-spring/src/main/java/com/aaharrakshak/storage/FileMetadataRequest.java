package com.aaharrakshak.storage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record FileMetadataRequest(
        @Size(max = 500) String objectKey,
        @Size(max = 180) String originalFileName,
        @Size(max = 120) String contentType,
        @Min(0) Long sizeBytes) {
}
