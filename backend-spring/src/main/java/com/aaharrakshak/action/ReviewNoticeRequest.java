package com.aaharrakshak.action;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewNoticeRequest(@NotBlank @Size(max = 1000) String notes) {
}
