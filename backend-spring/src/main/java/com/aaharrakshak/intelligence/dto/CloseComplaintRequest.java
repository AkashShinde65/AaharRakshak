package com.aaharrakshak.intelligence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CloseComplaintRequest(
        @NotBlank @Size(max = 500) String reason) {
}
