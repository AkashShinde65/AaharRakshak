package com.aaharrakshak.complaint.dto;

import java.time.LocalDate;

public record DetectedFoodDetails(
        String productName,
        String companyName,
        String fssaiLicenceNumber,
        String batchNumber,
        LocalDate expiryDate,
        double confidence,
        boolean uncertain) {
}
