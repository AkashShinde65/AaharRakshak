package com.aaharrakshak.company.dto;

import com.aaharrakshak.company.CompanyStatus;

public record CompanyProfileResponse(
        Long companyId,
        String legalName,
        String tradeName,
        String gstin,
        String registeredAddress,
        String contactEmail,
        String contactMobile,
        String websiteUrl,
        CompanyStatus status) {
}
