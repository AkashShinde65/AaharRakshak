package com.aaharrakshak.investigation.dto;

import com.aaharrakshak.investigation.SampleChainEventType;
import java.time.Instant;

public record SampleChainEventResponse(
        SampleChainEventType eventType,
        String locationText,
        String notes,
        Instant eventAt) {
}
