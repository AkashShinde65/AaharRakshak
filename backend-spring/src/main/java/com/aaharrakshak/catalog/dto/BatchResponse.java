package com.aaharrakshak.catalog.dto;

import com.aaharrakshak.catalog.BatchStatus;
import java.time.LocalDate;

public record BatchResponse(
        Long batchId,
        Long productId,
        String batchNumber,
        LocalDate manufacturedOn,
        LocalDate expiresOn,
        BatchStatus status) {
}
