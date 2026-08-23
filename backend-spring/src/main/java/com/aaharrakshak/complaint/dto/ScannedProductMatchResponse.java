package com.aaharrakshak.complaint.dto;

public record ScannedProductMatchResponse(
        Long productId,
        Long companyId,
        String productName,
        String companyName,
        String brand,
        String category,
        String barcode) {
}
