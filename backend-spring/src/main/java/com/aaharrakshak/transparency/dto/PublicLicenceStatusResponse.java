package com.aaharrakshak.transparency.dto;

import com.aaharrakshak.company.CompanyStatus;
import com.aaharrakshak.company.LicenceStatus;
import java.time.LocalDate;

public record PublicLicenceStatusResponse(
        String licenceNumber,
        String companyName,
        CompanyStatus companyStatus,
        LicenceStatus registryBackedStatus,
        String simulatedAdministrativeStatus,
        LocalDate validTo,
        String safetyNote) {
}
