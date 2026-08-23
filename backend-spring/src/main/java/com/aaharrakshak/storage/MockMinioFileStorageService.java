package com.aaharrakshak.storage;

import org.springframework.stereotype.Service;

@Service
public class MockMinioFileStorageService implements FileStorageService {

    @Override
    public StoredFileMetadata storeMetadata(String bucket, FileMetadataRequest request) {
        if (request == null || request.objectKey() == null || request.objectKey().isBlank()) {
            return null;
        }
        String normalizedBucket = normalizeBucket(bucket);
        String normalizedObjectKey = normalizeObjectKey(request.objectKey());
        return new StoredFileMetadata(
                normalizedBucket,
                normalizedObjectKey,
                blankToNull(request.originalFileName()),
                blankToNull(request.contentType()),
                request.sizeBytes(),
                "local-mock-minio://" + normalizedBucket + "/" + normalizedObjectKey);
    }

    private String normalizeBucket(String bucket) {
        if (bucket == null || bucket.isBlank()) {
            return "aaharrakshak";
        }
        return bucket.trim().toLowerCase();
    }

    private String normalizeObjectKey(String objectKey) {
        String normalized = objectKey.trim();
        if (normalized.startsWith("/") || normalized.contains("..")) {
            throw new IllegalArgumentException("Invalid object key");
        }
        return normalized;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
