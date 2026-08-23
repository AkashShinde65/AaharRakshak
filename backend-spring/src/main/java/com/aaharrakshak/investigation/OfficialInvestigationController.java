package com.aaharrakshak.investigation;

import com.aaharrakshak.investigation.dto.AssignInspectorRequest;
import com.aaharrakshak.investigation.dto.InspectionCheckInRequest;
import com.aaharrakshak.investigation.dto.InspectionScheduleRequest;
import com.aaharrakshak.investigation.dto.InspectionVisitRecordRequest;
import com.aaharrakshak.investigation.dto.InspectionVisitResponse;
import com.aaharrakshak.investigation.dto.InvestigationComplaintResponse;
import com.aaharrakshak.investigation.dto.InvestigationDashboardComplaintResponse;
import com.aaharrakshak.investigation.dto.LabAssignmentRequest;
import com.aaharrakshak.investigation.dto.LabAssignmentResponse;
import com.aaharrakshak.investigation.dto.LabReportResponse;
import com.aaharrakshak.investigation.dto.ReportReviewRequest;
import com.aaharrakshak.investigation.dto.SampleCollectionRequest;
import com.aaharrakshak.investigation.dto.SampleResponse;
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
@RequestMapping("/api/v1/official/investigations")
@Tag(name = "Official Investigations")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('FOOD_INSPECTOR','DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
public class OfficialInvestigationController {

    private final InvestigationService investigationService;

    public OfficialInvestigationController(InvestigationService investigationService) {
        this.investigationService = investigationService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Complaint-priority dashboard with map-ready coordinates and SLA due dates")
    List<InvestigationDashboardComplaintResponse> dashboard(@AuthenticationPrincipal AuthenticatedUser principal) {
        return investigationService.dashboard(principal);
    }

    @GetMapping("/assigned")
    @Operation(summary = "List only complaints assigned to the authenticated food inspector")
    List<InvestigationDashboardComplaintResponse> assigned(@AuthenticationPrincipal AuthenticatedUser principal) {
        return investigationService.assignedToInspector(principal);
    }

    @GetMapping("/complaints/{ticketNumber}")
    @Operation(summary = "View investigation details without citizen private contact details")
    InvestigationComplaintResponse complaint(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String ticketNumber) {
        return investigationService.complaint(principal, ticketNumber);
    }

    @PostMapping("/complaints/{ticketNumber}/verify")
    @PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
    @Operation(summary = "Verify a submitted complaint for official investigation")
    InvestigationComplaintResponse verifyComplaint(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String ticketNumber) {
        return investigationService.verifyComplaint(principal, ticketNumber);
    }

    @PostMapping("/complaints/{ticketNumber}/assign-inspector")
    @PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
    @Operation(summary = "Assign a complaint to a food inspector based on district/location")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "inspectorUserId": 3,
                      "district": "Pune",
                      "slaHours": 48,
                      "notes": "Assign to nearest inspector for market area visit"
                    }
                    """)))
    InvestigationComplaintResponse assignInspector(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String ticketNumber,
            @Valid @RequestBody AssignInspectorRequest request) {
        return investigationService.assignInspector(principal, ticketNumber, request);
    }

    @PostMapping("/complaints/{ticketNumber}/inspections/schedule")
    @Operation(summary = "Schedule an inspection visit for an assigned complaint")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "scheduledAt": "2026-08-01T10:30:00Z",
                      "locationText": "Pune demo market",
                      "notes": "Visit vendor location with sealed sample kit"
                    }
                    """)))
    InspectionVisitResponse scheduleInspection(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String ticketNumber,
            @Valid @RequestBody InspectionScheduleRequest request) {
        return investigationService.scheduleInspection(principal, ticketNumber, request);
    }

    @PostMapping("/inspections/{inspectionId}/check-in")
    @Operation(summary = "Record a geotagged inspection check-in")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "latitude": 18.52043,
                      "longitude": 73.85674,
                      "locationText": "Pune demo market"
                    }
                    """)))
    InspectionVisitResponse checkIn(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long inspectionId,
            @Valid @RequestBody InspectionCheckInRequest request) {
        return investigationService.checkIn(principal, inspectionId, request);
    }

    @PostMapping("/inspections/{inspectionId}/visit-record")
    @Operation(summary = "Record inspection notes, images and videos")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "visitedAt": "2026-08-01T11:00:00Z",
                      "notes": "Inspector observed storage and sealed one sample. Photos do not prove adulteration.",
                      "evidence": [
                        {
                          "type": "FOOD_PHOTO",
                          "objectKey": "inspection/demo/food.jpg",
                          "originalFileName": "food.jpg",
                          "contentType": "image/jpeg",
                          "sizeBytes": 98304,
                          "checksumSha256": "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd",
                          "capturedAt": "2026-08-01T10:55:00Z"
                        }
                      ]
                    }
                    """)))
    InspectionVisitResponse recordVisit(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long inspectionId,
            @Valid @RequestBody InspectionVisitRecordRequest request) {
        return investigationService.recordVisit(principal, inspectionId, request);
    }

    @PostMapping("/inspections/{inspectionId}/samples")
    @Operation(summary = "Collect a sealed sample and start chain-of-custody history")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "sealNumber": "SEAL-PUNE-0001",
                      "quantity": "250 g",
                      "collectedAt": "2026-08-01T11:15:00Z",
                      "latitude": 18.52043,
                      "longitude": 73.85674,
                      "locationText": "Pune demo market",
                      "storageDetails": "Sterile container, cold-box slot A2",
                      "chainOfCustodyNotes": "Collected and sealed in citizen complaint investigation"
                    }
                    """)))
    SampleResponse collectSample(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long inspectionId,
            @Valid @RequestBody SampleCollectionRequest request) {
        return investigationService.collectSample(principal, inspectionId, request);
    }

    @PostMapping("/samples/{sampleId}/assign-lab")
    @PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
    @Operation(summary = "Assign a collected sample to a laboratory officer")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "labOfficerUserId": 4,
                      "notes": "Assign to food chemistry bench"
                    }
                    """)))
    LabAssignmentResponse assignLab(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long sampleId,
            @Valid @RequestBody LabAssignmentRequest request) {
        return investigationService.assignLab(principal, sampleId, request);
    }

    @PostMapping("/lab-reports/{reportId}/review")
    @PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
    @Operation(summary = "Review a submitted laboratory report")
    LabReportResponse reviewReport(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long reportId,
            @Valid @RequestBody ReportReviewRequest request) {
        return investigationService.reviewLabReport(principal, reportId, request);
    }

    @PostMapping("/lab-reports/{reportId}/publish")
    @PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
    @Operation(summary = "Publish a reviewed laboratory report without imposing any automatic licence ban")
    LabReportResponse publishReport(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long reportId,
            @Valid @RequestBody ReportReviewRequest request) {
        return investigationService.publishLabReport(principal, reportId, request);
    }
}
