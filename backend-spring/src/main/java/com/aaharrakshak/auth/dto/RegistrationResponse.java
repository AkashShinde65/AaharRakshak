package com.aaharrakshak.auth.dto;

import com.aaharrakshak.company.CompanyStatus;
import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.UserStatus;
import java.util.Set;

public record RegistrationResponse(
        Long userId,
        Long companyId,
        String message,
        UserStatus userStatus,
        CompanyStatus companyStatus,
        Set<RoleName> roles) {
}

