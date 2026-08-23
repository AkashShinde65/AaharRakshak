package com.aaharrakshak.intelligence.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VendorReviewRequest(
        @NotNull Long companyId,
        Long productId,
        Long batchId,
        @Min(1) @Max(5) int rating,
        @Size(max = 1000) String reviewText,
        @Valid @NotNull ReceiptMetadataRequest receipt) {
}
