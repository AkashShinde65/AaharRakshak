package com.aaharrakshak.company.dto;

import com.aaharrakshak.company.LicenceStatus;
import java.time.LocalDate;

public record LicenceResponse(
        Long licenceId,
        Long companyId,
        String licenceNumber,
        String issuingAuthority,
        LocalDate validFrom,
        LocalDate validTo,
        LicenceStatus status,
        String registryStatus,
        String registryReferenceToken,
        String rejectionReason,
        String licenceLabelObjectKey,
        String licenceLabelFileName,
        String licenceLabelContentType,
        Long licenceLabelSizeBytes) {
}
