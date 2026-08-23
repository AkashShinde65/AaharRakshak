package com.aaharrakshak.auth.dto;

import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.UserStatus;
import java.util.Set;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        Long userId,
        String fullName,
        UserStatus status,
        Set<RoleName> roles) {
}

