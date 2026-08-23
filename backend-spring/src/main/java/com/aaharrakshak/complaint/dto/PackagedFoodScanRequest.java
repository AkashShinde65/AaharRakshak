package com.aaharrakshak.complaint.dto;

import com.aaharrakshak.storage.FileMetadataRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

public record PackagedFoodScanRequest(
        @Pattern(regexp = "\\d{8}|\\d{12}|\\d{13}|\\d{14}", message = "Barcode/GTIN must be 8, 12, 13 or 14 digits")
        String barcode,
        @Valid FileMetadataRequest frontLabelImage,
        @Valid FileMetadataRequest licenceLabelImage,
        @Valid FileMetadataRequest batchImage,
        @Valid FileMetadataRequest receiptImage) {
}
