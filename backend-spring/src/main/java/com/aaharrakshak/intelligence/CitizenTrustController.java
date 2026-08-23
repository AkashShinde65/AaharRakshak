package com.aaharrakshak.intelligence;

import com.aaharrakshak.intelligence.dto.AlertOutboxResponse;
import com.aaharrakshak.intelligence.dto.VendorReviewRequest;
import com.aaharrakshak.intelligence.dto.VendorReviewResponse;
import com.aaharrakshak.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/citizen")
@Tag(name = "Citizen Trust and Alerts")
@SecurityRequirement(name = "bearerAuth")
public class CitizenTrustController {

    private final TrustScoreService trustScoreService;
    private final AlertOutboxService alertOutboxService;

    public CitizenTrustController(TrustScoreService trustScoreService, AlertOutboxService alertOutboxService) {
        this.trustScoreService = trustScoreService;
        this.alertOutboxService = alertOutboxService;
    }

    @PostMapping("/trust/reviews")
    @Operation(summary = "Submit a receipt-backed verified vendor review")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "companyId": 1,
                      "productId": 1,
                      "batchId": 1,
                      "rating": 4,
                      "reviewText": "Receipt-backed purchase review for demo Trust Score.",
                      "receipt": {
                        "objectKey": "receipts/demo/receipt-0001.jpg",
                        "originalFileName": "receipt-0001.jpg",
                        "contentType": "image/jpeg",
                        "sizeBytes": 2048,
                        "checksumSha256": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                      }
                    }
                    """)))
    VendorReviewResponse submitReview(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody VendorReviewRequest request) {
        return trustScoreService.submitReview(principal, request);
    }

    @GetMapping("/alerts")
    @Operation(summary = "Authenticated citizen alert inbox sourced from durable outbox")
    List<AlertOutboxResponse> alerts(@AuthenticationPrincipal AuthenticatedUser principal) {
        return alertOutboxService.myAlerts(principal);
    }
}
