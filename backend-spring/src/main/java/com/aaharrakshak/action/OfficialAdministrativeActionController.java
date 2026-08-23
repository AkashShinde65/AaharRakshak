package com.aaharrakshak.action;

import com.aaharrakshak.action.dto.AdminActionDashboardItemResponse;
import com.aaharrakshak.action.dto.AdministrativeActionResponse;
import com.aaharrakshak.action.dto.ShowCauseNoticeResponse;
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
@RequestMapping("/api/v1/official/admin-actions")
@Tag(name = "Administrative Actions")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
public class OfficialAdministrativeActionController {

    private final AdministrativeActionService administrativeActionService;

    public OfficialAdministrativeActionController(AdministrativeActionService administrativeActionService) {
        this.administrativeActionService = administrativeActionService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Senior-official dashboard for published lab reports and administrative action status")
    List<AdminActionDashboardItemResponse> dashboard(@AuthenticationPrincipal AuthenticatedUser principal) {
        return administrativeActionService.dashboard(principal);
    }

    @GetMapping("/notices")
    @Operation(summary = "List show-cause notices and final action history")
    List<ShowCauseNoticeResponse> notices(@AuthenticationPrincipal AuthenticatedUser principal) {
        return administrativeActionService.notices(principal);
    }

    @GetMapping("/notices/{noticeNumber}")
    @Operation(summary = "View one show-cause notice with company response, action and history")
    ShowCauseNoticeResponse notice(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String noticeNumber) {
        return administrativeActionService.noticeDetails(principal, noticeNumber);
    }

    @PostMapping("/reports/{reportId}/show-cause-notices")
    @Operation(summary = "Issue a show-cause notice to the company for a published non-safe lab outcome")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "subject": "Show-cause notice for published laboratory report",
                      "reason": "Published mock lab outcome requires company explanation before any simulated action.",
                      "evidenceSummary": "Reviewed lab report, chain-of-custody and complaint records. No citizen details are shared.",
                      "responseDueAt": "2026-08-15T18:00:00Z"
                    }
                    """)))
    ShowCauseNoticeResponse issueNotice(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long reportId,
            @Valid @RequestBody IssueShowCauseNoticeRequest request) {
        return administrativeActionService.issueNotice(principal, reportId, request);
    }

    @PostMapping("/notices/{noticeNumber}/review")
    @Operation(summary = "Record senior-official review of the company response")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "notes": "Company response reviewed with public report and lab outcome before simulated decision."
                    }
                    """)))
    ShowCauseNoticeResponse reviewNotice(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String noticeNumber,
            @Valid @RequestBody ReviewNoticeRequest request) {
        return administrativeActionService.reviewNotice(principal, noticeNumber, request);
    }

    @PostMapping("/notices/{noticeNumber}/decision")
    @Operation(summary = "Record a simulated final administrative decision")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "actionType": "BATCH_RECALL",
                      "reason": "Company response did not resolve the published adulterated mock lab outcome.",
                      "evidenceSummary": "Decision is based on reviewed laboratory report and response documents.",
                      "effectiveDate": "2026-08-20",
                      "publicSummary": "Simulated recall notice for the affected demo batch. This is not a real government licence action."
                    }
                    """)))
    AdministrativeActionResponse decision(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String noticeNumber,
            @Valid @RequestBody AdministrativeDecisionRequest request) {
        return administrativeActionService.decide(principal, noticeNumber, request);
    }
}
