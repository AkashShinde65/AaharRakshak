package com.aaharrakshak.action;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record IssueShowCauseNoticeRequest(
        @NotBlank @Size(max = 180) String subject,
        @NotBlank @Size(max = 1000) String reason,
        @NotBlank @Size(max = 1200) String evidenceSummary,
        @NotNull @Future Instant responseDueAt) {
}
