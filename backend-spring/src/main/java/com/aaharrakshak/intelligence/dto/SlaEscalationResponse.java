package com.aaharrakshak.intelligence.dto;

import com.aaharrakshak.complaint.ComplaintStatus;
import java.time.Instant;

public record SlaEscalationResponse(
        Long escalationId,
        String ticketNumber,
        String district,
        Integer riskScore,
        ComplaintStatus previousStatus,
        String assignedInspectorName,
        String escalatedToName,
        String reason,
        Instant escalatedAt) {
}
