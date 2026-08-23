package com.aaharrakshak.storage;

public record StoredFileMetadata(
        String bucket,
        String objectKey,
        String originalFileName,
        String contentType,
        Long sizeBytes,
        String storageUri) {
}
