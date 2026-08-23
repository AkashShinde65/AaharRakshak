package com.aaharrakshak.complaint.dto;

public record PackagedFoodScanResponse(
        String barcode,
        boolean barcodeMatched,
        ScannedProductMatchResponse matchedProduct,
        DetectedFoodDetails ocrDetails,
        String safetyNote) {
}
