package com.aaharrakshak.investigation.dto;

import com.aaharrakshak.complaint.ComplaintCategory;
import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.complaint.ComplaintType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record InvestigationComplaintResponse(
        Long complaintId,
        String ticketNumber,
        ComplaintType complaintType,
        ComplaintCategory category,
        ComplaintStatus status,
        Integer riskScore,
        String district,
        Instant slaDueAt,
        Boolean overdue,
        BigDecimal latitude,
        BigDecimal longitude,
        String address,
        String vendorName,
        String vendorAddress,
        String productName,
        String companyName,
        Long assignedInspectorId,
        String assignedInspectorName,
        List<InspectionVisitResponse> inspections,
        List<SampleResponse> samples) {
}
