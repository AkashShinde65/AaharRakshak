package com.aaharrakshak.investigation;

import com.aaharrakshak.investigation.dto.LabAssignmentResponse;
import com.aaharrakshak.investigation.dto.LabReportResponse;
import com.aaharrakshak.investigation.dto.LabReportUploadRequest;
import com.aaharrakshak.investigation.dto.SampleReceivedRequest;
import com.aaharrakshak.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/lab/investigations")
@Tag(name = "Laboratory Investigations")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('LABORATORY_OFFICER')")
public class LabInvestigationController {

    private final InvestigationService investigationService;

    public LabInvestigationController(InvestigationService investigationService) {
        this.investigationService = investigationService;
    }

    @GetMapping("/samples/assigned")
    @Operation(summary = "List samples assigned to the authenticated laboratory officer")
    List<LabAssignmentResponse> assignedSamples(@AuthenticationPrincipal AuthenticatedUser principal) {
        return investigationService.assignedLabSamples(principal);
    }

    @PostMapping("/samples/{sampleId}/received")
    @Operation(summary = "Confirm laboratory receipt of an assigned sample")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "locationText": "Demo State Food Lab",
                      "storageCondition": "Cold storage rack C1, seal intact",
                      "notes": "Received with matching seal number"
                    }
                    """)))
    LabAssignmentResponse receiveSample(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long sampleId,
            @Valid @RequestBody SampleReceivedRequest request) {
        return investigationService.receiveSample(principal, sampleId, request);
    }

    @PostMapping("/samples/{sampleId}/reports/drafts")
    @Operation(summary = "Create a draft PDF lab report with testing parameters and checksums")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "reportNumber": "LAB-PUNE-0001",
                      "objectKey": "lab-reports/LAB-PUNE-0001.pdf",
                      "originalFileName": "LAB-PUNE-0001.pdf",
                      "contentType": "application/pdf",
                      "sizeBytes": 262144,
                      "checksumSha256": "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
                      "resultSummary": "Parameters within demo limits",
                      "results": [
                        {
                          "parameterName": "Moisture",
                          "testMethod": "Mock IS method",
                          "permissibleLimit": "< 12%",
                          "resultValue": "8.2",
                          "unit": "%",
                          "compliant": true,
                          "remarks": "Mock academic result"
                        }
                      ]
                    }
                    """)))
    LabReportResponse createDraft(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long sampleId,
            @Valid @RequestBody LabReportUploadRequest request) {
        return investigationService.createLabReportDraft(principal, sampleId, request);
    }

    @PostMapping("/reports/{reportId}/submit")
    @Operation(summary = "Submit a draft lab report for senior official review")
    LabReportResponse submitReport(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long reportId) {
        return investigationService.submitLabReport(principal, reportId);
    }
}
