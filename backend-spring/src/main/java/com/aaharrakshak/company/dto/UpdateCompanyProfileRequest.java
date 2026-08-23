package com.aaharrakshak.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCompanyProfileRequest(
        @NotBlank @Size(max = 180) String legalName,
        @Size(max = 180) String tradeName,
        @Size(max = 30) String gstin,
        @Size(max = 300) String registeredAddress,
        @Email @Size(max = 160) String contactEmail,
        @Size(max = 20) String contactMobile,
        @Size(max = 180) String websiteUrl) {
}
