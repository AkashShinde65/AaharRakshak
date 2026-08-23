package com.aaharrakshak.complaint.dto;

import com.aaharrakshak.complaint.ComplaintCategory;
import com.aaharrakshak.complaint.ComplaintType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record ComplaintDraftRequest(
        @NotNull ComplaintType complaintType,
        @NotNull ComplaintCategory category,
        @Pattern(regexp = "\\d{8}|\\d{12}|\\d{13}|\\d{14}", message = "Barcode/GTIN must be 8, 12, 13 or 14 digits")
        String scannedBarcode,
        Long productId,
        Long batchId,
        @Size(max = 180) String detectedProductName,
        @Size(max = 180) String detectedCompanyName,
        @Pattern(regexp = "\\d{14}", message = "FSSAI licence number must contain exactly 14 digits")
        String detectedFssaiLicenceNumber,
        @Size(max = 80) String detectedBatchNumber,
        LocalDate detectedExpiryDate,
        @Size(max = 180) String confirmedProductName,
        @Size(max = 180) String confirmedCompanyName,
        @Pattern(regexp = "\\d{14}", message = "FSSAI licence number must contain exactly 14 digits")
        String confirmedFssaiLicenceNumber,
        @Size(max = 80) String confirmedBatchNumber,
        LocalDate confirmedExpiryDate,
        @Size(max = 180) String vendorName,
        @Size(max = 300) String vendorAddress,
        @Size(max = 1000) String description,
        @Valid GpsLocationRequest location,
        @Valid List<EvidenceMetadataRequest> evidence) {
}
