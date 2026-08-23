package com.aaharrakshak.auth.dto;

import com.aaharrakshak.auth.OtpChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpRequest(
        @NotBlank String identifier,
        @NotNull OtpChannel channel) {
}

