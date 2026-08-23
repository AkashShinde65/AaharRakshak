package com.aaharrakshak.transparency.dto;

import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.complaint.ComplaintType;
import java.time.Instant;
import java.util.List;

public record PublicComplaintStatusResponse(
        String ticketNumber,
        ComplaintType complaintType,
        ComplaintStatus status,
        String category,
        String companyName,
        String productName,
        String batchNumber,
        String district,
        Instant submittedAt,
        Instant updatedAt,
        List<PublicLabReportResponse> publishedReports) {
}
