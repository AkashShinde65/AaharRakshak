package com.aaharrakshak.investigation.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SampleResponse(
        Long sampleId,
        String sampleNumber,
        String sealNumber,
        String ticketNumber,
        String quantity,
        Instant collectedAt,
        BigDecimal latitude,
        BigDecimal longitude,
        String locationText,
        String storageDetails,
        List<SampleChainEventResponse> chainOfCustody) {
}
