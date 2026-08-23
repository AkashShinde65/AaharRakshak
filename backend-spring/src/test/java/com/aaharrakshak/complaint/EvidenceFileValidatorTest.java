package com.aaharrakshak.complaint;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aaharrakshak.complaint.dto.EvidenceMetadataRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class EvidenceFileValidatorTest {

    private final EvidenceFileValidator validator = new EvidenceFileValidator();

    @Test
    void acceptsSafeImageEvidence() {
        assertThatCode(() -> validator.validate(new EvidenceMetadataRequest(
                EvidenceType.PRODUCT_LABEL_PHOTO,
                "safe/product-label.jpg",
                "product-label.jpg",
                "image/jpeg",
                1024L,
                "a".repeat(64),
                Instant.now().minus(1, ChronoUnit.MINUTES))))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsUnsafeKeysTypesSizesAndFutureTimestamps() {
        assertThatThrownBy(() -> validator.validate(new EvidenceMetadataRequest(
                EvidenceType.FOOD_PHOTO,
                "../unsafe.jpg",
                "unsafe.jpg",
                "image/jpeg",
                1024L,
                "a".repeat(64),
                Instant.now())))
                .isInstanceOf(ResponseStatusException.class);

        assertThatThrownBy(() -> validator.validate(new EvidenceMetadataRequest(
                EvidenceType.VIDEO,
                "safe/video.exe",
                "video.exe",
                "application/octet-stream",
                1024L,
                "a".repeat(64),
                Instant.now())))
                .isInstanceOf(ResponseStatusException.class);

        assertThatThrownBy(() -> validator.validate(new EvidenceMetadataRequest(
                EvidenceType.RECEIPT_FILE,
                "safe/receipt.pdf",
                "receipt.pdf",
                "application/pdf",
                11L * 1024L * 1024L,
                "a".repeat(64),
                Instant.now())))
                .isInstanceOf(ResponseStatusException.class);

        assertThatThrownBy(() -> validator.validate(new EvidenceMetadataRequest(
                EvidenceType.FOOD_PHOTO,
                "safe/photo.jpg",
                "photo.jpg",
                "image/jpeg",
                1024L,
                "a".repeat(64),
                Instant.now().plus(10, ChronoUnit.MINUTES))))
                .isInstanceOf(ResponseStatusException.class);
    }
}
