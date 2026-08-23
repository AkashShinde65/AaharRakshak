package com.aaharrakshak.complaint.dto;

import com.aaharrakshak.complaint.ComplaintCategory;
import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.complaint.ComplaintType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ComplaintResponse(
        Long complaintId,
        String ticketNumber,
        ComplaintType complaintType,
        ComplaintCategory category,
        ComplaintStatus status,
        String description,
        String scannedBarcode,
        Long productId,
        String productName,
        Long companyId,
        String companyName,
        Long batchId,
        String batchNumber,
        String detectedProductName,
        String detectedCompanyName,
        String detectedFssaiLicenceNumber,
        String detectedBatchNumber,
        LocalDate detectedExpiryDate,
        String confirmedProductName,
        String confirmedCompanyName,
        String confirmedFssaiLicenceNumber,
        String confirmedBatchNumber,
        LocalDate confirmedExpiryDate,
        String vendorName,
        String vendorAddress,
        Boolean gpsConsent,
        BigDecimal latitude,
        BigDecimal longitude,
        String address,
        Integer riskScore,
        Instant createdAt,
        Instant submittedAt,
        List<EvidenceResponse> evidence,
        List<ComplaintStatusHistoryResponse> statusHistory,
        String safetyNote) {
}
