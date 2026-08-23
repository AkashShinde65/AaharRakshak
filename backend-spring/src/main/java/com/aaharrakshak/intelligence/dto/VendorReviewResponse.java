package com.aaharrakshak.intelligence.dto;

import java.time.Instant;

public record VendorReviewResponse(
        Long reviewId,
        Long companyId,
        Integer rating,
        Boolean receiptVerified,
        String receiptVerificationToken,
        Instant createdAt,
        String moderationNote) {
}
