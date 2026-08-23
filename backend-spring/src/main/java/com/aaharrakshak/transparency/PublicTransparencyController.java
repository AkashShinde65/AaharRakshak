package com.aaharrakshak.transparency;

import com.aaharrakshak.transparency.dto.PublicAdministrativeActionResponse;
import com.aaharrakshak.transparency.dto.PublicBatchStatusResponse;
import com.aaharrakshak.transparency.dto.PublicComplaintStatusResponse;
import com.aaharrakshak.transparency.dto.PublicLabReportResponse;
import com.aaharrakshak.transparency.dto.PublicLicenceStatusResponse;
import com.aaharrakshak.transparency.dto.PublicSearchResultResponse;
import com.aaharrakshak.transparency.dto.SafetyAlertResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/transparency")
@Tag(name = "Public Transparency")
public class PublicTransparencyController {

    private final TransparencyService transparencyService;

    public PublicTransparencyController(TransparencyService transparencyService) {
        this.transparencyService = transparencyService;
    }

    @GetMapping("/complaints/{ticketNumber}/status")
    @Operation(summary = "Track public-safe complaint status by ticket number")
    PublicComplaintStatusResponse complaintStatus(@PathVariable String ticketNumber) {
        return transparencyService.complaintStatus(ticketNumber);
    }

    @GetMapping("/reports/{reportNumber}")
    @Operation(summary = "View anonymized published laboratory report")
    PublicLabReportResponse report(@PathVariable String reportNumber) {
        return transparencyService.report(reportNumber);
    }

    @GetMapping("/search")
    @Operation(summary = "Public search by complaint number, company, product, batch or location")
    List<PublicSearchResultResponse> search(
            @RequestParam(required = false) String complaintNumber,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String product,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String location) {
        return transparencyService.search(complaintNumber, company, product, batch, location);
    }

    @GetMapping("/licences/{licenceNumber}/status")
    @Operation(summary = "Public licence status with simulated administrative status note")
    PublicLicenceStatusResponse licenceStatus(@PathVariable String licenceNumber) {
        return transparencyService.licenceStatus(licenceNumber);
    }

    @GetMapping("/batches/{batchNumber}/status")
    @Operation(summary = "Public batch status")
    PublicBatchStatusResponse batchStatus(@PathVariable String batchNumber) {
        return transparencyService.batchStatus(batchNumber);
    }

    @GetMapping("/recalls")
    @Operation(summary = "Public simulated recall notices")
    List<PublicAdministrativeActionResponse> recalls() {
        return transparencyService.recallNotices();
    }

    @GetMapping("/alerts")
    @Operation(summary = "Public safety alerts")
    List<SafetyAlertResponse> alerts() {
        return transparencyService.safetyAlerts();
    }
}
