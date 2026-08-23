package com.aaharrakshak.catalog;

import com.aaharrakshak.catalog.dto.PublicProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/public/products")
@Tag(name = "Public Product Lookup")
public class PublicProductController {

    private final CatalogService catalogService;

    public PublicProductController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/barcodes/{barcode}")
    @Operation(summary = "Look up public product details by barcode or GTIN")
    PublicProductResponse byBarcode(@PathVariable String barcode) {
        return catalogService.publicLookupByBarcode(barcode);
    }

    @GetMapping("/search")
    @Operation(summary = "Search public product details by product name")
    List<PublicProductResponse> search(@RequestParam String query) {
        return catalogService.publicSearchByName(query);
    }
}
