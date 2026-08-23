package com.aaharrakshak.auth;

import com.aaharrakshak.auth.dto.AuthResponse;
import com.aaharrakshak.auth.dto.LoginRequest;
import com.aaharrakshak.auth.dto.MockAadhaarVerificationRequest;
import com.aaharrakshak.auth.dto.OtpRequest;
import com.aaharrakshak.auth.dto.OtpResponse;
import com.aaharrakshak.auth.dto.RefreshTokenRequest;
import com.aaharrakshak.auth.dto.RegisterCitizenRequest;
import com.aaharrakshak.auth.dto.RegisterCompanyRequest;
import com.aaharrakshak.auth.dto.RegistrationResponse;
import com.aaharrakshak.auth.dto.UserProfileResponse;
import com.aaharrakshak.auth.dto.VerificationResponse;
import com.aaharrakshak.auth.dto.VerifyOtpRequest;
import com.aaharrakshak.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/citizen")
    @ResponseStatus(HttpStatus.CREATED)
    RegistrationResponse registerCitizen(@Valid @RequestBody RegisterCitizenRequest request) {
        return authService.registerCitizen(request);
    }

    @PostMapping("/register/company")
    @ResponseStatus(HttpStatus.CREATED)
    RegistrationResponse registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {
        return authService.registerCompany(request);
    }

    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/otp/request")
    OtpResponse requestOtp(@Valid @RequestBody OtpRequest request) {
        return authService.requestOtp(request);
    }

    @PostMapping("/otp/verify")
    VerificationResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return authService.verifyOtp(request);
    }

    @PostMapping("/mock-aadhaar/verify")
    VerificationResponse verifyMockAadhaar(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody MockAadhaarVerificationRequest request) {
        return authService.verifyMockAadhaar(principal, request);
    }

    @GetMapping("/me")
    UserProfileResponse me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return authService.profile(principal);
    }
}

