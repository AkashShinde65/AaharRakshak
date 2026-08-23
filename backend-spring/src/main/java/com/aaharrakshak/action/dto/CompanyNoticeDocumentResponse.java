package com.aaharrakshak.action.dto;

public record CompanyNoticeDocumentResponse(
        String objectKey,
        String originalFileName,
        String contentType,
        Long sizeBytes,
        String checksumSha256) {
}
