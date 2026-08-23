package com.aaharrakshak.admin;

import com.aaharrakshak.audit.AuditService;
import com.aaharrakshak.company.Company;
import com.aaharrakshak.company.CompanyRepository;
import com.aaharrakshak.security.AuthenticatedUser;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final CompanyRepository companyRepository;
    private final AuditService auditService;

    public AdminController(CompanyRepository companyRepository, AuditService auditService) {
        this.companyRepository = companyRepository;
        this.auditService = auditService;
    }

    @GetMapping("/dashboard")
    Map<String, String> dashboard() {
        return Map.of("role", "CENTRAL_ADMINISTRATOR", "status", "AUTHORIZED");
    }

    @PostMapping("/companies/{companyId}/verify")
    @Transactional
    Map<String, Object> verifyCompany(
            @PathVariable Long companyId,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        company.verify();
        auditService.record(principal.getUser(), "COMPANY_VERIFIED", "COMPANY", company.getId().toString(),
                "Administrative verification");
        return Map.of(
                "companyId", company.getId(),
                "status", company.getStatus().name());
    }
}

