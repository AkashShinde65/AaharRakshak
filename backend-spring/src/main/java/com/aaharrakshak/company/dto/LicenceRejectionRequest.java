package com.aaharrakshak.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LicenceRejectionRequest(
        @NotBlank @Size(max = 500) String reason) {
}
