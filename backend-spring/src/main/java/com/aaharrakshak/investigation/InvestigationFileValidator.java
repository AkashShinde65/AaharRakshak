package com.aaharrakshak.investigation;

import com.aaharrakshak.complaint.EvidenceType;
import com.aaharrakshak.investigation.dto.InvestigationFileMetadataRequest;
import com.aaharrakshak.investigation.dto.LabReportUploadRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class InvestigationFileValidator {

    private static final long TEN_MB = 10L * 1024L * 1024L;
    private static final long FIFTY_MB = 50L * 1024L * 1024L;
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> VIDEO_TYPES = Set.of("video/mp4", "video/quicktime");

    public void validateInspectionEvidence(InvestigationFileMetadataRequest request) {
        validateObjectKey(request.objectKey());
        validateFutureTimestamp(request.capturedAt());
        String contentType = request.contentType().toLowerCase(Locale.ROOT);
        if (request.type() == EvidenceType.VIDEO) {
            requireContentType(contentType, VIDEO_TYPES);
            requireSize(request.sizeBytes(), FIFTY_MB);
            return;
        }
        requireContentType(contentType, IMAGE_TYPES);
        requireSize(request.sizeBytes(), TEN_MB);
    }

    public void validateLabReport(LabReportUploadRequest request) {
        validateObjectKey(request.objectKey());
        requireContentType(request.contentType().toLowerCase(Locale.ROOT), Set.of("application/pdf"));
        requireSize(request.sizeBytes(), TEN_MB);
    }

    private void validateObjectKey(String objectKey) {
        if (objectKey.startsWith("/") || objectKey.contains("..") || objectKey.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Object key is not safe");
        }
    }

    private void validateFutureTimestamp(Instant capturedAt) {
        if (capturedAt.isAfter(Instant.now().plus(5, ChronoUnit.MINUTES))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evidence timestamp cannot be in the future");
        }
    }

    private void requireContentType(String actual, Set<String> allowed) {
        if (!allowed.contains(actual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File type is not allowed");
        }
    }

    private void requireSize(long sizeBytes, long maxBytes) {
        if (sizeBytes > maxBytes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File exceeds allowed size");
        }
    }
}
