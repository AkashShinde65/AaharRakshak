package com.aaharrakshak.action;

import com.aaharrakshak.action.dto.ShowCauseNoticeResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company/admin-actions")
@Tag(name = "Company Notices")
@SecurityRequirement(name = "bearerAuth")
public class CompanyAdministrativeActionController {

    private final AdministrativeActionService administrativeActionService;

    public CompanyAdministrativeActionController(AdministrativeActionService administrativeActionService) {
        this.administrativeActionService = administrativeActionService;
    }

    @GetMapping("/notices")
    @Operation(summary = "Company dashboard for show-cause notices and decisions")
    List<ShowCauseNoticeResponse> notices(@AuthenticationPrincipal AuthenticatedUser principal) {
        return administrativeActionService.companyNotices(principal);
    }

    @GetMapping("/notices/{noticeNumber}")
    @Operation(summary = "Company can view only its own notice")
    ShowCauseNoticeResponse notice(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String noticeNumber) {
        return administrativeActionService.noticeDetails(principal, noticeNumber);
    }

    @PostMapping("/notices/{noticeNumber}/responses")
    @Operation(summary = "Submit company response and supporting document metadata")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "responseText": "We reviewed the mock lab finding and submit corrective records for official review.",
                      "document": {
                        "objectKey": "company-responses/SCN-DEMO-0001/response.pdf",
                        "originalFileName": "response.pdf",
                        "contentType": "application/pdf",
                        "sizeBytes": 262144,
                        "checksumSha256": "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                      }
                    }
                    """)))
    ShowCauseNoticeResponse respond(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String noticeNumber,
            @Valid @RequestBody CompanyNoticeResponseRequest request) {
        return administrativeActionService.submitCompanyResponse(principal, noticeNumber, request);
    }
}
