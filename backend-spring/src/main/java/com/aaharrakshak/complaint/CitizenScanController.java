package com.aaharrakshak.complaint;

import com.aaharrakshak.complaint.dto.PackagedFoodScanRequest;
import com.aaharrakshak.complaint.dto.PackagedFoodScanResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/citizen/scans")
@Tag(name = "Citizen Food Scanning")
@SecurityRequirement(name = "bearerAuth")
public class CitizenScanController {

    private final FoodScanService foodScanService;

    public CitizenScanController(FoodScanService foodScanService) {
        this.foodScanService = foodScanService;
    }

    @PostMapping("/packaged-food")
    @Operation(
            summary = "Scan packaged food using barcode lookup first, then mock OCR hints",
            description = "Returns catalogue matches before manual entry and OCR hints that a citizen may correct. "
                    + "Image/OCR output is not treated as proof of adulteration.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                    name = "Packaged food scan",
                    value = """
                            {
                              "barcode": "8901234567890",
                              "frontLabelImage": {
                                "objectKey": "citizen-uploads/scan/front-label.jpg",
                                "originalFileName": "front-label.jpg",
                                "contentType": "image/jpeg",
                                "sizeBytes": 143210
                              },
                              "licenceLabelImage": {
                                "objectKey": "citizen-uploads/scan/licence-label.jpg",
                                "originalFileName": "licence-label.jpg",
                                "contentType": "image/jpeg",
                                "sizeBytes": 121005
                              }
                            }
                            """)))
    PackagedFoodScanResponse scanPackagedFood(@Valid @RequestBody PackagedFoodScanRequest request) {
        return foodScanService.scanPackagedFood(request);
    }
}
