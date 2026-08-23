package com.aaharrakshak.action;

import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AdministrativeDocumentValidator {

    private static final long MAX_RESPONSE_DOCUMENT_BYTES = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png");

    public void validate(AdministrativeDocumentRequest document) {
        String contentType = document.contentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Response document must be PDF, JPEG or PNG");
        }
        if (document.sizeBytes() > MAX_RESPONSE_DOCUMENT_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Response document exceeds 10 MB");
        }
        String objectKey = document.objectKey();
        if (objectKey.startsWith("/") || objectKey.contains("..") || objectKey.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsafe response document object key");
        }
    }
}
