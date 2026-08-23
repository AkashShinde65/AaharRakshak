package com.aaharrakshak.company;

import com.aaharrakshak.company.dto.CompanyProfileResponse;
import com.aaharrakshak.company.dto.LicenceResponse;
import com.aaharrakshak.company.dto.LicenceSubmissionRequest;
import com.aaharrakshak.company.dto.UpdateCompanyProfileRequest;
import com.aaharrakshak.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company")
@Tag(name = "Company Portal")
@SecurityRequirement(name = "bearerAuth")
public class CompanyPortalController {

    private final CompanyService companyService;

    public CompanyPortalController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get the authenticated company's profile")
    CompanyProfileResponse profile(@AuthenticationPrincipal AuthenticatedUser principal) {
        return companyService.myProfile(principal);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update the authenticated company's profile")
    CompanyProfileResponse updateProfile(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody UpdateCompanyProfileRequest request) {
        return companyService.updateProfile(principal, request);
    }

    @PostMapping("/licences")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a 14-digit FSSAI licence for official review")
    LicenceResponse submitLicence(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody LicenceSubmissionRequest request) {
        return companyService.submitLicence(principal, request);
    }

    @GetMapping("/licences")
    @Operation(summary = "List licences submitted by the authenticated company")
    List<LicenceResponse> licences(@AuthenticationPrincipal AuthenticatedUser principal) {
        return companyService.myLicences(principal);
    }
}
