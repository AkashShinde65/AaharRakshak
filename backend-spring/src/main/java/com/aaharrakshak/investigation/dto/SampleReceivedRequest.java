package com.aaharrakshak.investigation.dto;

import jakarta.validation.constraints.Size;

public record SampleReceivedRequest(
        @Size(max = 220) String locationText,
        @Size(max = 500) String storageCondition,
        @Size(max = 500) String notes) {
}
