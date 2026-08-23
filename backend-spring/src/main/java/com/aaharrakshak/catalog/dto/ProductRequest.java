package com.aaharrakshak.catalog.dto;

import com.aaharrakshak.storage.FileMetadataRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank @Size(max = 180) String name,
        @Size(max = 120) String brand,
        @Size(max = 80) String category,
        @Size(max = 180) String manufacturerName,
        @Pattern(regexp = "\\d{8}|\\d{12}|\\d{13}|\\d{14}", message = "Barcode/GTIN must be 8, 12, 13 or 14 digits")
        String primaryBarcode,
        @Size(max = 1000) String description,
        @Valid FileMetadataRequest frontLabelImage,
        @Valid FileMetadataRequest licenceLabelImage) {
}
