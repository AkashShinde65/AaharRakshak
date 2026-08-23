package com.aaharrakshak.company.dto;

import com.aaharrakshak.company.FssaiLicenceNumber;
import com.aaharrakshak.storage.FileMetadataRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record LicenceSubmissionRequest(
        @FssaiLicenceNumber String licenceNumber,
        @Size(max = 120) String issuingAuthority,
        LocalDate validFrom,
        LocalDate validTo,
        @Valid FileMetadataRequest licenceLabelImage) {
}
