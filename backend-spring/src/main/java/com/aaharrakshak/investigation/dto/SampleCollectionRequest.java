package com.aaharrakshak.investigation.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

public record SampleCollectionRequest(
        @NotBlank @Size(max = 80) String sealNumber,
        @NotBlank @Size(max = 80) String quantity,
        @NotNull Instant collectedAt,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
        @NotBlank @Size(max = 220) String locationText,
        @NotBlank @Size(max = 500) String storageDetails,
        @Size(max = 1000) String chainOfCustodyNotes) {
}
