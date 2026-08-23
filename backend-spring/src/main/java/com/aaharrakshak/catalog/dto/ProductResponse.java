package com.aaharrakshak.catalog.dto;

import java.util.List;

public record ProductResponse(
        Long productId,
        Long companyId,
        String name,
        String brand,
        String category,
        String manufacturerName,
        String primaryBarcode,
        String description,
        String frontLabelObjectKey,
        String frontLabelFileName,
        String frontLabelContentType,
        Long frontLabelSizeBytes,
        String licenceLabelObjectKey,
        String licenceLabelFileName,
        String licenceLabelContentType,
        Long licenceLabelSizeBytes,
        List<String> barcodes) {
}
