package com.aaharrakshak.catalog;

import com.aaharrakshak.audit.AuditService;
import com.aaharrakshak.catalog.dto.BatchRequest;
import com.aaharrakshak.catalog.dto.BatchResponse;
import com.aaharrakshak.catalog.dto.ProductRequest;
import com.aaharrakshak.catalog.dto.ProductResponse;
import com.aaharrakshak.catalog.dto.PublicProductResponse;
import com.aaharrakshak.company.Company;
import com.aaharrakshak.company.CompanyService;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.storage.FileMetadataRequest;
import com.aaharrakshak.storage.FileStorageService;
import com.aaharrakshak.storage.StoredFileMetadata;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogService {

    private final ProductRepository productRepository;
    private final ProductBarcodeRepository productBarcodeRepository;
    private final BatchRepository batchRepository;
    private final CompanyService companyService;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;

    public CatalogService(
            ProductRepository productRepository,
            ProductBarcodeRepository productBarcodeRepository,
            BatchRepository batchRepository,
            CompanyService companyService,
            FileStorageService fileStorageService,
            AuditService auditService) {
        this.productRepository = productRepository;
        this.productBarcodeRepository = productBarcodeRepository;
        this.batchRepository = batchRepository;
        this.companyService = companyService;
        this.fileStorageService = fileStorageService;
        this.auditService = auditService;
    }

    @Transactional
    public ProductResponse createProduct(AuthenticatedUser principal, ProductRequest request) {
        Company company = companyService.loadOwnedCompany(principal);
        ensureBarcodeAvailable(request.primaryBarcode(), null);
        Product product = productRepository.save(new Product(
                company,
                request.name(),
                request.primaryBarcode(),
                request.category(),
                request.brand(),
                request.manufacturerName(),
                request.description(),
                storeMetadata("product-front-labels", request.frontLabelImage()),
                storeMetadata("product-licence-labels", request.licenceLabelImage())));
        ensurePrimaryBarcode(product, request.primaryBarcode());
        auditService.record(principal.getUser(), "PRODUCT_CREATED", "PRODUCT", product.getId().toString(),
                "Company created product");
        return toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> myProducts(AuthenticatedUser principal) {
        return productRepository.findByCompanyOwnerUserIdOrderByNameAsc(principal.getUserId()).stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse myProduct(AuthenticatedUser principal, Long productId) {
        return toProductResponse(loadOwnedProduct(principal, productId));
    }

    @Transactional
    public ProductResponse updateProduct(AuthenticatedUser principal, Long productId, ProductRequest request) {
        Product product = loadOwnedProduct(principal, productId);
        ensureBarcodeAvailable(request.primaryBarcode(), product.getId());
        product.update(
                request.name(),
                request.primaryBarcode(),
                request.category(),
                request.brand(),
                request.manufacturerName(),
                request.description(),
                storeMetadata("product-front-labels", request.frontLabelImage()),
                storeMetadata("product-licence-labels", request.licenceLabelImage()));
        ensurePrimaryBarcode(product, request.primaryBarcode());
        auditService.record(principal.getUser(), "PRODUCT_UPDATED", "PRODUCT", product.getId().toString(),
                "Company updated product");
        return toProductResponse(product);
    }

    @Transactional
    public BatchResponse createBatch(AuthenticatedUser principal, Long productId, BatchRequest request) {
        Product product = loadOwnedProduct(principal, productId);
        validateBatchDates(request);
        Batch batch = batchRepository.save(new Batch(
                product,
                request.batchNumber(),
                request.manufacturedOn(),
                request.expiresOn(),
                request.status()));
        auditService.record(principal.getUser(), "BATCH_CREATED", "BATCH", batch.getId().toString(),
                "Company created batch");
        return toBatchResponse(batch);
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> batchesForProduct(AuthenticatedUser principal, Long productId) {
        loadOwnedProduct(principal, productId);
        return batchRepository.findByProductIdOrderByExpiresOnAsc(productId).stream()
                .map(this::toBatchResponse)
                .toList();
    }

    @Transactional
    public BatchResponse updateBatch(AuthenticatedUser principal, Long batchId, BatchRequest request) {
        Batch batch = batchRepository.findByIdAndProductCompanyOwnerUserId(batchId, principal.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));
        validateBatchDates(request);
        batch.update(request.batchNumber(), request.manufacturedOn(), request.expiresOn(), request.status());
        auditService.record(principal.getUser(), "BATCH_UPDATED", "BATCH", batch.getId().toString(),
                "Company updated batch");
        return toBatchResponse(batch);
    }

    @Transactional(readOnly = true)
    public PublicProductResponse publicLookupByBarcode(String barcode) {
        ProductBarcode productBarcode = productBarcodeRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barcode not found"));
        return toPublicProductResponse(productBarcode.getProduct());
    }

    @Transactional(readOnly = true)
    public List<PublicProductResponse> publicSearchByName(String query) {
        if (query == null || query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query is required");
        }
        return productRepository.findByNameContainingIgnoreCaseOrderByNameAsc(query.trim()).stream()
                .map(this::toPublicProductResponse)
                .toList();
    }

    private Product loadOwnedProduct(AuthenticatedUser principal, Long productId) {
        return productRepository.findByIdAndCompanyOwnerUserId(productId, principal.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private void validateBatchDates(BatchRequest request) {
        if (request.expiresOn().isBefore(request.manufacturedOn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expiry date cannot be before manufacturing date");
        }
    }

    private void ensureBarcodeAvailable(String barcode, Long currentProductId) {
        if (barcode == null || barcode.isBlank()) {
            return;
        }
        productBarcodeRepository.findByBarcode(barcode).ifPresent(existing -> {
            if (!existing.getProduct().getId().equals(currentProductId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Barcode already maps to another product");
            }
        });
    }

    private void ensurePrimaryBarcode(Product product, String barcode) {
        if (barcode == null || barcode.isBlank()) {
            return;
        }
        if (productBarcodeRepository.findByBarcode(barcode).isEmpty()) {
            productBarcodeRepository.save(new ProductBarcode(
                    product,
                    barcode,
                    barcodeTypeFor(barcode),
                    true));
        }
    }

    private ProductBarcodeType barcodeTypeFor(String barcode) {
        return switch (barcode.length()) {
            case 8 -> ProductBarcodeType.GTIN_8;
            case 12 -> ProductBarcodeType.GTIN_12;
            case 13 -> ProductBarcodeType.GTIN_13;
            case 14 -> ProductBarcodeType.GTIN_14;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported barcode length");
        };
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCompany().getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory(),
                product.getManufacturerName(),
                product.getBarcode(),
                product.getDescription(),
                product.getFrontLabelObjectKey(),
                product.getFrontLabelFileName(),
                product.getFrontLabelContentType(),
                product.getFrontLabelSizeBytes(),
                product.getLicenceLabelObjectKey(),
                product.getLicenceLabelFileName(),
                product.getLicenceLabelContentType(),
                product.getLicenceLabelSizeBytes(),
                barcodesFor(product));
    }

    private BatchResponse toBatchResponse(Batch batch) {
        return new BatchResponse(
                batch.getId(),
                batch.getProduct().getId(),
                batch.getBatchNumber(),
                batch.getManufacturedOn(),
                batch.getExpiresOn(),
                batch.getStatus());
    }

    private PublicProductResponse toPublicProductResponse(Product product) {
        Company company = product.getCompany();
        return new PublicProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory(),
                product.getManufacturerName(),
                product.getBarcode(),
                barcodesFor(product),
                company.getId(),
                company.getLegalName(),
                company.getTradeName(),
                company.getStatus());
    }

    private List<String> barcodesFor(Product product) {
        return productBarcodeRepository.findByProductIdOrderByPrimaryCodeDescBarcodeAsc(product.getId()).stream()
                .map(ProductBarcode::getBarcode)
                .toList();
    }

    private StoredFileMetadata storeMetadata(String bucket, FileMetadataRequest request) {
        try {
            return fileStorageService.storeMetadata(bucket, request);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
