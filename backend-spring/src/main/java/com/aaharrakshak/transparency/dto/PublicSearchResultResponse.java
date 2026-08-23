package com.aaharrakshak.transparency.dto;

import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.investigation.LabOutcome;
import java.time.Instant;

public record PublicSearchResultResponse(
        String ticketNumber,
        ComplaintStatus status,
        String companyName,
        String productName,
        String batchNumber,
        String district,
        LabOutcome latestOutcome,
        Instant latestPublishedAt) {
}
