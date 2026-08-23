package com.aaharrakshak.action.dto;

import java.time.Instant;

public record CompanyNoticeResponseResponse(
        Long responseId,
        String responseText,
        CompanyNoticeDocumentResponse document,
        Instant submittedAt) {
}
