package com.aaharrakshak.investigation.dto;

import jakarta.validation.constraints.Size;

public record ReportReviewRequest(@Size(max = 500) String notes) {
}
