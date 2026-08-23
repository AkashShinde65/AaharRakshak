package com.aaharrakshak.complaint;

import com.aaharrakshak.complaint.dto.EvidenceMetadataRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EvidenceFileValidator {

    private static final long TEN_MB = 10L * 1024L * 1024L;
    private static final long FIFTY_MB = 50L * 1024L * 1024L;
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> VIDEO_TYPES = Set.of("video/mp4", "video/quicktime");
    private static final Set<String> RECEIPT_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "application/pdf");

    public void validate(EvidenceMetadataRequest request) {
        validateObjectKey(request.objectKey());
        validateCapturedAt(request.capturedAt());
        String contentType = request.contentType().toLowerCase();
        long maxSize = maxSizeFor(request.type());
        if (request.sizeBytes() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evidence file exceeds allowed size");
        }
        if (!allowedContentTypes(request.type()).contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evidence file type is not allowed");
        }
    }

    private void validateObjectKey(String objectKey) {
        if (objectKey.startsWith("/") || objectKey.contains("..") || objectKey.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evidence object key is not safe");
        }
    }

    private void validateCapturedAt(Instant capturedAt) {
        if (capturedAt.isAfter(Instant.now().plus(5, ChronoUnit.MINUTES))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evidence timestamp cannot be in the future");
        }
    }

    private Set<String> allowedContentTypes(EvidenceType type) {
        return switch (type) {
            case VIDEO -> VIDEO_TYPES;
            case RECEIPT_PHOTO, RECEIPT_FILE -> RECEIPT_TYPES;
            case LAB_REPORT_FILE -> Set.of("application/pdf");
            default -> IMAGE_TYPES;
        };
    }

    private long maxSizeFor(EvidenceType type) {
        return type == EvidenceType.VIDEO ? FIFTY_MB : TEN_MB;
    }
}
