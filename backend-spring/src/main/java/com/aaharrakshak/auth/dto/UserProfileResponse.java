package com.aaharrakshak.auth.dto;

import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.UserStatus;
import java.util.Set;

public record UserProfileResponse(
        Long userId,
        String fullName,
        String email,
        String mobileNumber,
        UserStatus status,
        boolean emailVerified,
        boolean mobileVerified,
        String identityVerificationStatus,
        Set<RoleName> roles) {
}

