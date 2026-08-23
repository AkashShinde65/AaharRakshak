package com.aaharrakshak.complaint;

import com.aaharrakshak.complaint.dto.ComplaintDraftRequest;
import com.aaharrakshak.complaint.dto.ComplaintResponse;
import com.aaharrakshak.complaint.dto.EvidenceMetadataRequest;
import com.aaharrakshak.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/api/v1/citizen/complaints")
@Tag(name = "Citizen Complaints")
@SecurityRequirement(name = "bearerAuth")
public class CitizenComplaintController {

    private final ComplaintService complaintService;

    public CitizenComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a complaint draft after scanning and confirming details",
            description = "Supports packaged food complaints with catalogue links and unknown product/vendor "
                    + "complaints where citizens provide corrected details. Citizen identity is never returned.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = {
                    @ExampleObject(
                            name = "Packaged food draft",
                            value = """
                                    {
                                      "complaintType": "PACKAGED_FOOD",
                                      "category": "SUSPECTED_ADULTERATION",
                                      "scannedBarcode": "8901234567890",
                                      "productId": 1,
                                      "detectedProductName": "Demo Turmeric Powder",
                                      "detectedCompanyName": "Demo Foods Private Limited",
                                      "detectedFssaiLicenceNumber": "12345678901234",
                                      "detectedBatchNumber": "TUR-2026-001",
                                      "detectedExpiryDate": "2027-01-14",
                                      "confirmedProductName": "Demo Turmeric Powder",
                                      "confirmedCompanyName": "Demo Foods Private Limited",
                                      "confirmedFssaiLicenceNumber": "12345678901234",
                                      "confirmedBatchNumber": "TUR-2026-001",
                                      "confirmedExpiryDate": "2027-01-14",
                                      "description": "Label text looked different from the package I expected.",
                                      "location": {
                                        "consentAccepted": true,
                                        "latitude": 18.52043,
                                        "longitude": 73.85674,
                                        "address": "Pune demo market"
                                      },
                                      "evidence": [
                                        {
                                          "type": "PRODUCT_LABEL_PHOTO",
                                          "objectKey": "complaints/demo/product-label.jpg",
                                          "originalFileName": "product-label.jpg",
                                          "contentType": "image/jpeg",
                                          "sizeBytes": 143210,
                                          "checksumSha256": "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                                          "capturedAt": "2026-01-01T10:00:00Z"
                                        }
                                      ]
                                    }
                                    """),
                    @ExampleObject(
                            name = "Prepared dish draft",
                            value = """
                                    {
                                      "complaintType": "PREPARED_DISH",
                                      "category": "HYGIENE_ISSUE",
                                      "vendorName": "Unknown Chaat Vendor",
                                      "vendorAddress": "Near demo bus stand",
                                      "description": "Prepared dish complaint with vendor and dish images.",
                                      "location": {
                                        "consentAccepted": true,
                                        "latitude": 18.52043,
                                        "longitude": 73.85674,
                                        "address": "Pune demo market"
                                      },
                                      "evidence": [
                                        {
                                          "type": "DISH_IMAGE",
                                          "objectKey": "complaints/demo/dish.jpg",
                                          "originalFileName": "dish.jpg",
                                          "contentType": "image/jpeg",
                                          "sizeBytes": 118990,
                                          "checksumSha256": "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
                                          "capturedAt": "2026-01-01T10:00:00Z"
                                        },
                                        {
                                          "type": "VENDOR_IMAGE",
                                          "objectKey": "complaints/demo/vendor.jpg",
                                          "originalFileName": "vendor.jpg",
                                          "contentType": "image/jpeg",
                                          "sizeBytes": 111000,
                                          "checksumSha256": "1111111111111111111111111111111111111111111111111111111111111111",
                                          "capturedAt": "2026-01-01T10:01:00Z"
                                        }
                                      ]
                                    }
                                    """)
            }))
    ComplaintResponse createDraft(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody ComplaintDraftRequest request) {
        return complaintService.createDraft(principal, request);
    }

    @PutMapping("/{complaintId}/draft")
    @Operation(
            summary = "Correct or update a complaint draft",
            description = "Citizens may correct uncertain OCR or barcode-derived details while the complaint remains a draft.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                    name = "Correct OCR details",
                    value = """
                            {
                              "complaintType": "PACKAGED_FOOD",
                              "category": "MISLABELING",
                              "scannedBarcode": "8901234567890",
                              "confirmedProductName": "Corrected product name",
                              "confirmedCompanyName": "Corrected company name",
                              "confirmedFssaiLicenceNumber": "12345678901234",
                              "confirmedBatchNumber": "TUR-2026-001",
                              "confirmedExpiryDate": "2027-01-14",
                              "description": "Citizen corrected uncertain scan details before submitting.",
                              "location": {
                                "consentAccepted": true,
                                "latitude": 18.52043,
                                "longitude": 73.85674,
                                "address": "Pune demo market"
                              }
                            }
                            """)))
    ComplaintResponse updateDraft(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long complaintId,
            @Valid @RequestBody ComplaintDraftRequest request) {
        return complaintService.updateDraft(principal, complaintId, request);
    }

    @PostMapping("/{complaintId}/evidence")
    @Operation(
            summary = "Add validated evidence metadata to a draft complaint",
            description = "Accepts image, video, receipt and supporting-file metadata after type, size, object-key and checksum validation.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                    name = "Receipt evidence",
                    value = """
                            {
                              "type": "RECEIPT_FILE",
                              "objectKey": "complaints/demo/receipt.pdf",
                              "originalFileName": "receipt.pdf",
                              "contentType": "application/pdf",
                              "sizeBytes": 84220,
                              "checksumSha256": "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc",
                              "capturedAt": "2026-01-01T10:02:00Z"
                            }
                            """)))
    ComplaintResponse addEvidence(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long complaintId,
            @Valid @RequestBody EvidenceMetadataRequest request) {
        return complaintService.addEvidence(principal, complaintId, request);
    }

    @PostMapping("/{complaintId}/submit")
    @Operation(summary = "Submit a draft complaint and receive a tracking number")
    ComplaintResponse submit(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long complaintId) {
        return complaintService.submit(principal, complaintId);
    }

    @GetMapping
    @Operation(summary = "List only the authenticated citizen's complaints")
    List<ComplaintResponse> myComplaints(@AuthenticationPrincipal AuthenticatedUser principal) {
        return complaintService.myComplaints(principal);
    }

    @GetMapping("/{ticketNumber}")
    @Operation(summary = "Get one complaint owned by the authenticated citizen")
    ComplaintResponse myComplaint(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String ticketNumber) {
        return complaintService.myComplaint(principal, ticketNumber);
    }
}
