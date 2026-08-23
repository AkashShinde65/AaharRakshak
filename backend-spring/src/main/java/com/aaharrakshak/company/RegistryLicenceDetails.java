package com.aaharrakshak.company;

import java.time.LocalDate;

public record RegistryLicenceDetails(
        boolean verified,
        String licenceNumber,
        String status,
        String issuingAuthority,
        LocalDate validFrom,
        LocalDate validTo,
        String referenceToken,
        String message) {
}
