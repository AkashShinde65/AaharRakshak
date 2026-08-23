package com.aaharrakshak.investigation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssignInspectorRequest(
        @NotNull Long inspectorUserId,
        @Size(max = 120) String district,
        @Min(1) @Max(720) Integer slaHours,
        @Size(max = 500) String notes) {
}
