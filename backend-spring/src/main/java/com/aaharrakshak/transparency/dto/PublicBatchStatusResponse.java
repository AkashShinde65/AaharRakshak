package com.aaharrakshak.transparency.dto;

import com.aaharrakshak.catalog.BatchStatus;
import java.time.LocalDate;

public record PublicBatchStatusResponse(
        String batchNumber,
        String productName,
        String companyName,
        BatchStatus platformStatus,
        LocalDate manufacturedOn,
        LocalDate expiresOn,
        String safetyNote) {
}
