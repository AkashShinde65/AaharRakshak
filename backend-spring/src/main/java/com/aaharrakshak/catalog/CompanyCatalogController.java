package com.aaharrakshak.catalog;

import com.aaharrakshak.catalog.dto.BatchRequest;
import com.aaharrakshak.catalog.dto.BatchResponse;
import com.aaharrakshak.catalog.dto.ProductRequest;
import com.aaharrakshak.catalog.dto.ProductResponse;
import com.aaharrakshak.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company")
@Tag(name = "Company Catalogue")
@SecurityRequirement(name = "bearerAuth")
public class CompanyCatalogController {

    private final CatalogService catalogService;

    public CompanyCatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a product owned by the authenticated company")
    ProductResponse createProduct(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody ProductRequest request) {
        return catalogService.createProduct(principal, request);
    }

    @GetMapping("/products")
    @Operation(summary = "List products owned by the authenticated company")
    List<ProductResponse> products(@AuthenticationPrincipal AuthenticatedUser principal) {
        return catalogService.myProducts(principal);
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "Get a company-owned product")
    ProductResponse product(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long productId) {
        return catalogService.myProduct(principal, productId);
    }

    @PutMapping("/products/{productId}")
    @Operation(summary = "Update a company-owned product")
    ProductResponse updateProduct(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request) {
        return catalogService.updateProduct(principal, productId, request);
    }

    @PostMapping("/products/{productId}/batches")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a batch for a company-owned product")
    BatchResponse createBatch(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long productId,
            @Valid @RequestBody BatchRequest request) {
        return catalogService.createBatch(principal, productId, request);
    }

    @GetMapping("/products/{productId}/batches")
    @Operation(summary = "List batches for a company-owned product")
    List<BatchResponse> batches(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long productId) {
        return catalogService.batchesForProduct(principal, productId);
    }

    @PutMapping("/batches/{batchId}")
    @Operation(summary = "Update a company-owned batch")
    BatchResponse updateBatch(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long batchId,
            @Valid @RequestBody BatchRequest request) {
        return catalogService.updateBatch(principal, batchId, request);
    }
}
