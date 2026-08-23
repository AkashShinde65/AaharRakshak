package com.aaharrakshak.action;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class AdministrativeDocumentValidatorTest {

    private final AdministrativeDocumentValidator validator = new AdministrativeDocumentValidator();

    @Test
    void acceptsSafePdfMetadata() {
        validator.validate(new AdministrativeDocumentRequest(
                "company-responses/demo/response.pdf",
                "response.pdf",
                "application/pdf",
                2048L,
                "a".repeat(64)));
    }

    @Test
    void rejectsUnsafeObjectKeysAndOversizedFiles() {
        assertThatThrownBy(() -> validator.validate(new AdministrativeDocumentRequest(
                "../secret.pdf",
                "secret.pdf",
                "application/pdf",
                2048L,
                "a".repeat(64))))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Unsafe response document object key");

        assertThatThrownBy(() -> validator.validate(new AdministrativeDocumentRequest(
                "company-responses/demo/large.pdf",
                "large.pdf",
                "application/pdf",
                11L * 1024 * 1024,
                "a".repeat(64))))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("exceeds 10 MB");
    }
}
