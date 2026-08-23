package com.aaharrakshak.transparency.dto;

import java.time.Instant;

public record SafetyAlertResponse(
        Long alertId,
        String title,
        String message,
        String severity,
        String companyName,
        String productName,
        String batchNumber,
        String location,
        Instant publishedAt) {
}
