package com.aaharrakshak.auth;

import com.aaharrakshak.audit.AuditService;
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
import com.aaharrakshak.company.Company;
import com.aaharrakshak.company.CompanyRepository;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.security.JwtService;
import com.aaharrakshak.security.UserPrincipalService;
import com.aaharrakshak.user.Role;
import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.RoleRepository;
import com.aaharrakshak.user.User;
import com.aaharrakshak.user.UserRepository;
import com.aaharrakshak.user.UserRole;
import com.aaharrakshak.user.UserRoleRepository;
import com.aaharrakshak.user.UserStatus;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final String MOCK_OTP_CODE = "123456";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyRepository companyRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserPrincipalService userPrincipalService;
    private final AuditService auditService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final int maxLoginAttempts;
    private final int lockMinutes;
    private final int refreshTokenDays;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            CompanyRepository companyRepository,
            OtpVerificationRepository otpVerificationRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserPrincipalService userPrincipalService,
            AuditService auditService,
            @Value("${app.security.max-login-attempts}") int maxLoginAttempts,
            @Value("${app.security.lock-minutes}") int lockMinutes,
            @Value("${app.security.refresh-token-days}") int refreshTokenDays) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.companyRepository = companyRepository;
        this.otpVerificationRepository = otpVerificationRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userPrincipalService = userPrincipalService;
        this.auditService = auditService;
        this.maxLoginAttempts = maxLoginAttempts;
        this.lockMinutes = lockMinutes;
        this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public RegistrationResponse registerCitizen(RegisterCitizenRequest request) {
        ensureUniqueUser(request.email(), request.mobileNumber());
        User user = userRepository.save(new User(
                request.fullName(),
                request.email().toLowerCase(),
                request.mobileNumber(),
                passwordEncoder.encode(request.password()),
                UserStatus.PENDING_VERIFICATION));
        assignRole(user, RoleName.CITIZEN);
        auditService.record(user, "CITIZEN_REGISTERED", "USER", user.getId().toString(), "Citizen self-registration");
        return new RegistrationResponse(
                user.getId(),
                null,
                "Citizen registered. Verify email or mobile OTP before login.",
                user.getStatus(),
                null,
                Set.of(RoleName.CITIZEN));
    }

    @Transactional
    public RegistrationResponse registerCompany(RegisterCompanyRequest request) {
        ensureUniqueUser(request.email(), request.mobileNumber());
        User user = userRepository.save(new User(
                request.contactFullName(),
                request.email().toLowerCase(),
                request.mobileNumber(),
                passwordEncoder.encode(request.password()),
                UserStatus.ACTIVE));
        user.markEmailVerified();
        user.markMobileVerified();
        assignRole(user, RoleName.COMPANY);
        Company company = companyRepository.save(new Company(
                request.legalName(),
                request.tradeName(),
                request.gstin(),
                user));
        auditService.record(user, "COMPANY_REGISTERED", "COMPANY", company.getId().toString(),
                "Company registration pending official verification");
        return new RegistrationResponse(
                user.getId(),
                company.getId(),
                "Company registered with PENDING_VERIFICATION status.",
                user.getStatus(),
                company.getStatus(),
                Set.of(RoleName.COMPANY));
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public AuthResponse login(LoginRequest request) {
        User user = findByIdentifier(request.identifier());
        Instant now = Instant.now();
        if (user.isLocked(now)) {
            auditService.record(user, "LOGIN_BLOCKED_LOCKED_ACCOUNT", "USER", user.getId().toString(), "Account locked");
            throw new ResponseStatusException(HttpStatus.LOCKED, "Account temporarily locked");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.recordFailedLogin(maxLoginAttempts, now.plus(lockMinutes, ChronoUnit.MINUTES));
            userRepository.save(user);
            auditService.record(user, "LOGIN_FAILED", "USER", user.getId().toString(), "Invalid password");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            auditService.record(user, "LOGIN_BLOCKED_STATUS", "USER", user.getId().toString(), user.getStatus().name());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }
        user.recordSuccessfulLogin();
        Set<RoleName> roles = userPrincipalService.rolesFor(user);
        AuthResponse response = issueTokens(user, roles);
        auditService.record(user, "LOGIN_SUCCESS", "USER", user.getId().toString(), "Successful login");
        return response;
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = sha256(request.refreshToken());
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        if (!refreshToken.isUsable(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }
        refreshToken.revoke();
        User user = refreshToken.getUser();
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }
        AuthResponse response = issueTokens(user, userPrincipalService.rolesFor(user));
        auditService.record(user, "TOKEN_REFRESHED", "USER", user.getId().toString(), "Refresh token rotated");
        return response;
    }

    @Transactional
    public OtpResponse requestOtp(OtpRequest request) {
        User user = findByIdentifier(request.identifier());
        String destination = destinationFor(user, request.channel());
        otpVerificationRepository.save(new OtpVerification(
                user,
                request.channel(),
                destination,
                MOCK_OTP_CODE,
                Instant.now().plus(10, ChronoUnit.MINUTES)));
        auditService.record(user, "OTP_REQUESTED", "USER", user.getId().toString(), request.channel().name());
        return new OtpResponse(destination, MOCK_OTP_CODE, "Mock OTP generated for development.");
    }

    @Transactional
    public VerificationResponse verifyOtp(VerifyOtpRequest request) {
        User user = findByIdentifier(request.identifier());
        String destination = destinationFor(user, request.channel());
        OtpVerification otp = otpVerificationRepository
                .findFirstByDestinationAndChannelAndVerifiedFalseOrderByCreatedAtDesc(destination, request.channel())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No active OTP"));
        if (otp.getExpiresAt().isBefore(Instant.now()) || !otp.getCode().equals(request.code())) {
            auditService.record(user, "OTP_VERIFICATION_FAILED", "USER", user.getId().toString(), request.channel().name());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");
        }
        otp.markVerified();
        if (request.channel() == OtpChannel.EMAIL) {
            user.markEmailVerified();
        } else {
            user.markMobileVerified();
        }
        auditService.record(user, "OTP_VERIFIED", "USER", user.getId().toString(), request.channel().name());
        return new VerificationResponse("VERIFIED", null);
    }

    @Transactional
    public VerificationResponse verifyMockAadhaar(
            AuthenticatedUser principal,
            MockAadhaarVerificationRequest request) {
        if (!request.consentAccepted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Consent is required");
        }
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        String token = "mock-aadhaar-" + randomToken(18);
        user.markMockAadhaarVerified(token);
        auditService.record(user, "MOCK_AADHAAR_VERIFIED", "USER", user.getId().toString(),
                "Stored verification status/token only");
        return new VerificationResponse(user.getIdentityVerificationStatus(), user.getIdentityVerificationToken());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse profile(AuthenticatedUser principal) {
        User user = principal.getUser();
        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getStatus(),
                user.getEmailVerified(),
                user.getMobileVerified(),
                user.getIdentityVerificationStatus(),
                principal.getRoles());
    }

    private void ensureUniqueUser(String email, String mobileNumber) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number is already registered");
        }
    }

    private User findByIdentifier(String identifier) {
        String normalized = identifier.trim();
        return (normalized.contains("@")
                ? userRepository.findByEmailIgnoreCase(normalized)
                : userRepository.findByMobileNumber(normalized))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }

    private void assignRole(User user, RoleName roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Missing role " + roleName));
        userRoleRepository.save(new UserRole(user, role));
    }

    private String destinationFor(User user, OtpChannel channel) {
        return switch (channel) {
            case EMAIL -> user.getEmail();
            case MOBILE -> user.getMobileNumber();
        };
    }

    private AuthResponse issueTokens(User user, Set<RoleName> roles) {
        String accessToken = jwtService.createAccessToken(user, roles);
        String refreshToken = randomToken(48);
        refreshTokenRepository.save(new RefreshToken(
                user,
                sha256(refreshToken),
                Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS)));
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenSeconds(),
                user.getId(),
                user.getFullName(),
                user.getStatus(),
                roles);
    }

    private String randomToken(int byteCount) {
        byte[] bytes = new byte[byteCount];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not hash token", ex);
        }
    }
}
