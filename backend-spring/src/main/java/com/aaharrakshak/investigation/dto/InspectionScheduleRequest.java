package com.aaharrakshak.investigation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record InspectionScheduleRequest(
        @NotNull Instant scheduledAt,
        @Size(max = 220) String locationText,
        @Size(max = 500) String notes) {
}
