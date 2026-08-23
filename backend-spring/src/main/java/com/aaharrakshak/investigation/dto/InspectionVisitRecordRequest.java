package com.aaharrakshak.investigation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public record InspectionVisitRecordRequest(
        Instant visitedAt,
        @Size(max = 1500) String notes,
        @Valid List<InvestigationFileMetadataRequest> evidence) {
}
