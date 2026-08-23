package com.aaharrakshak.storage;

public interface FileStorageService {

    StoredFileMetadata storeMetadata(String bucket, FileMetadataRequest request);
}
