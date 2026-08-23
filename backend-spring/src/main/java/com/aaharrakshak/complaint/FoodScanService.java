package com.aaharrakshak.complaint;

import com.aaharrakshak.catalog.Product;
import com.aaharrakshak.catalog.ProductBarcode;
import com.aaharrakshak.catalog.ProductBarcodeRepository;
import com.aaharrakshak.complaint.dto.DetectedFoodDetails;
import com.aaharrakshak.complaint.dto.PackagedFoodScanRequest;
import com.aaharrakshak.complaint.dto.PackagedFoodScanResponse;
import com.aaharrakshak.complaint.dto.ScannedProductMatchResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodScanService {

    public static final String IMAGE_SAFETY_NOTE =
            "Image and OCR results are only detection aids. They do not prove adulteration; inspection and lab testing are required.";

    private final ProductBarcodeRepository productBarcodeRepository;
    private final OcrAdapter ocrAdapter;

    public FoodScanService(ProductBarcodeRepository productBarcodeRepository, OcrAdapter ocrAdapter) {
        this.productBarcodeRepository = productBarcodeRepository;
        this.ocrAdapter = ocrAdapter;
    }

    @Transactional(readOnly = true)
    public PackagedFoodScanResponse scanPackagedFood(PackagedFoodScanRequest request) {
        ScannedProductMatchResponse matchedProduct = null;
        if (request.barcode() != null && !request.barcode().isBlank()) {
            matchedProduct = productBarcodeRepository.findByBarcode(request.barcode())
                    .map(ProductBarcode::getProduct)
                    .map(this::toMatchResponse)
                    .orElse(null);
        }
        DetectedFoodDetails detectedDetails = ocrAdapter.detectPackagedFoodDetails(request);
        return new PackagedFoodScanResponse(
                request.barcode(),
                matchedProduct != null,
                matchedProduct,
                detectedDetails,
                IMAGE_SAFETY_NOTE);
    }

    private ScannedProductMatchResponse toMatchResponse(Product product) {
        return new ScannedProductMatchResponse(
                product.getId(),
                product.getCompany().getId(),
                product.getName(),
                product.getCompany().getLegalName(),
                product.getBrand(),
                product.getCategory(),
                product.getBarcode());
    }
}
