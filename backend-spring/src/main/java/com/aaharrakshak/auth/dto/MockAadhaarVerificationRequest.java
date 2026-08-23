package com.aaharrakshak.auth.dto;

import jakarta.validation.constraints.AssertTrue;

public record MockAadhaarVerificationRequest(
        @AssertTrue(message = "Consent is required for mock Aadhaar verification") boolean consentAccepted) {
}

