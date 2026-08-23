package com.aaharrakshak.security;

import com.aaharrakshak.auth.dto.UserProfileResponse;
import com.aaharrakshak.auth.AuthService;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccessProbeController {

    private final AuthService authService;

    public AccessProbeController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/api/v1/citizen/profile")
    UserProfileResponse citizenProfile(@AuthenticationPrincipal AuthenticatedUser principal) {
        return authService.profile(principal);
    }

    @GetMapping("/api/v1/company/account")
    UserProfileResponse companyAccount(@AuthenticationPrincipal AuthenticatedUser principal) {
        return authService.profile(principal);
    }

    @GetMapping("/api/v1/official/inspectors/dashboard")
    Map<String, String> inspectorDashboard() {
        return Map.of("role", "FOOD_INSPECTOR", "status", "AUTHORIZED");
    }

    @GetMapping("/api/v1/official/lab/dashboard")
    Map<String, String> labDashboard() {
        return Map.of("role", "LABORATORY_OFFICER", "status", "AUTHORIZED");
    }

    @GetMapping("/api/v1/official/district/dashboard")
    Map<String, String> districtDashboard() {
        return Map.of("role", "DISTRICT_ESCALATION_OFFICER", "status", "AUTHORIZED");
    }
}
