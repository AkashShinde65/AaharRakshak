package com.aaharrakshak.investigation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LabAssignmentRequest(
        @NotNull Long labOfficerUserId,
        @Size(max = 500) String notes) {
}
