package com.aaharrakshak.action;

import com.aaharrakshak.investigation.ActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AdministrativeDecisionRequest(
        @NotNull ActionType actionType,
        @NotBlank @Size(max = 1000) String reason,
        @NotBlank @Size(max = 1200) String evidenceSummary,
        @NotNull LocalDate effectiveDate,
        @NotBlank @Size(max = 1000) String publicSummary) {
}
