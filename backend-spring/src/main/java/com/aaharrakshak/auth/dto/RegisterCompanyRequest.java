package com.aaharrakshak.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCompanyRequest(
        @NotBlank @Size(max = 120) String contactFullName,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 20) String mobileNumber,
        @NotBlank @Size(min = 8, max = 80) String password,
        @NotBlank @Size(max = 180) String legalName,
        @Size(max = 180) String tradeName,
        @Size(max = 30) String gstin) {
}

