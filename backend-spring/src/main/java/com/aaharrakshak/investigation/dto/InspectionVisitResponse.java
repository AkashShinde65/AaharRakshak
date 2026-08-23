package com.aaharrakshak.investigation.dto;

import com.aaharrakshak.investigation.InspectionVisitStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record InspectionVisitResponse(
        Long inspectionId,
        String ticketNumber,
        Long inspectorUserId,
        String inspectorName,
        Instant scheduledAt,
        InspectionVisitStatus status,
        Instant checkInAt,
        BigDecimal checkInLatitude,
        BigDecimal checkInLongitude,
        String locationText,
        String visitNotes,
        Instant completedAt,
        List<InspectionEvidenceResponse> evidence) {
}
