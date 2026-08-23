package com.aaharrakshak.action.dto;

import com.aaharrakshak.action.AdministrativeNoticeStatus;
import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.investigation.ActionType;
import com.aaharrakshak.investigation.LabOutcome;
import java.time.Instant;

public record AdminActionDashboardItemResponse(
        Long reportId,
        String reportNumber,
        String ticketNumber,
        ComplaintStatus complaintStatus,
        LabOutcome outcome,
        String companyName,
        String productName,
        String batchNumber,
        String district,
        String noticeNumber,
        AdministrativeNoticeStatus noticeStatus,
        ActionType actionType,
        Instant responseDueAt,
        Instant publishedAt) {
}
