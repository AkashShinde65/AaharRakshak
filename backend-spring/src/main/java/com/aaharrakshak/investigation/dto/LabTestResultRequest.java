package com.aaharrakshak.investigation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LabTestResultRequest(
        @NotBlank @Size(max = 120) String parameterName,
        @Size(max = 120) String testMethod,
        @Size(max = 80) String permissibleLimit,
        @NotBlank @Size(max = 80) String resultValue,
        @Size(max = 40) String unit,
        @NotNull Boolean compliant,
        @Size(max = 500) String remarks) {
}
