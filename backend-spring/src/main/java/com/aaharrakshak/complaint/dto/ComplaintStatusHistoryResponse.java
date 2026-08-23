package com.aaharrakshak.complaint.dto;

import com.aaharrakshak.complaint.ComplaintStatus;
import java.time.Instant;

public record ComplaintStatusHistoryResponse(
        ComplaintStatus status,
        String notes,
        Instant createdAt) {
}
