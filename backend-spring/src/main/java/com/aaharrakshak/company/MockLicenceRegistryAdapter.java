package com.aaharrakshak.company;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class MockLicenceRegistryAdapter implements LicenceRegistryAdapter {

    @Override
    public RegistryLicenceDetails lookup(String licenceNumber) {
        if (licenceNumber == null || !licenceNumber.matches("\\d{14}")) {
            return rejected(licenceNumber, "INVALID_FORMAT", "Mock registry accepts exactly 14 digits.");
        }
        if (licenceNumber.endsWith("0000")) {
            return rejected(licenceNumber, "NOT_FOUND", "Mock registry has no matching licence.");
        }
        LocalDate now = LocalDate.now();
        return new RegistryLicenceDetails(
                true,
                licenceNumber,
                "VALID",
                "Mock FSSAI Licence Registry",
                now.minusYears(1),
                now.plusYears(2),
                "mock-fssai-" + licenceNumber.substring(licenceNumber.length() - 6),
                "Verified by deterministic mock registry adapter.");
    }

    private RegistryLicenceDetails rejected(String licenceNumber, String status, String message) {
        return new RegistryLicenceDetails(
                false,
                licenceNumber,
                status,
                "Mock FSSAI Licence Registry",
                null,
                null,
                "mock-fssai-rejected",
                message);
    }
}
