package com.aaharrakshak.catalog.dto;

import com.aaharrakshak.company.CompanyStatus;
import java.util.List;

public record PublicProductResponse(
        Long productId,
        String name,
        String brand,
        String category,
        String manufacturerName,
        String primaryBarcode,
        List<String> barcodes,
        Long companyId,
        String companyName,
        String tradeName,
        CompanyStatus companyStatus) {
}
