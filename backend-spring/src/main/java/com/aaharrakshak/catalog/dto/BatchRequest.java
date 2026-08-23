package com.aaharrakshak.catalog.dto;

import com.aaharrakshak.catalog.BatchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record BatchRequest(
        @NotBlank @Size(max = 80) String batchNumber,
        @NotNull LocalDate manufacturedOn,
        @NotNull LocalDate expiresOn,
        BatchStatus status) {
}
