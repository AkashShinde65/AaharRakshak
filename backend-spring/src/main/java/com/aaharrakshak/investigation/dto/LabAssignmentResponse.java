package com.aaharrakshak.investigation.dto;

import com.aaharrakshak.investigation.SampleLabAssignmentStatus;
import java.time.Instant;

public record LabAssignmentResponse(
        Long assignmentId,
        Long sampleId,
        String sampleNumber,
        String ticketNumber,
        Long labOfficerUserId,
        String labOfficerName,
        SampleLabAssignmentStatus status,
        Instant assignedAt,
        Instant receivedAt,
        String notes) {
}
