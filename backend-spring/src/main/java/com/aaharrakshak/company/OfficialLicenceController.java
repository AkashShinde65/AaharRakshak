package com.aaharrakshak.company;

import com.aaharrakshak.company.dto.LicenceRejectionRequest;
import com.aaharrakshak.company.dto.LicenceResponse;
import com.aaharrakshak.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/official/licences")
@Tag(name = "Official Licence Review")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('FOOD_INSPECTOR','DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
public class OfficialLicenceController {

    private final CompanyService companyService;

    public OfficialLicenceController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/{licenceId}/verify")
    @Operation(summary = "Verify a submitted licence using the mock registry adapter")
    LicenceResponse verify(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long licenceId) {
        return companyService.verifyLicence(licenceId, principal);
    }

    @PostMapping("/{licenceId}/reject")
    @Operation(summary = "Reject a submitted licence")
    LicenceResponse reject(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long licenceId,
            @Valid @RequestBody LicenceRejectionRequest request) {
        return companyService.rejectLicence(licenceId, principal, request);
    }

    @PostMapping("/{licenceId}/expire")
    @Operation(summary = "Mark a licence as expired")
    LicenceResponse expire(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long licenceId) {
        return companyService.expireLicence(licenceId, principal);
    }
}
