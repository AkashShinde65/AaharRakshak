package com.aaharrakshak.action;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompanyNoticeResponseRequest(
        @NotBlank @Size(max = 3000) String responseText,
        @NotNull @Valid AdministrativeDocumentRequest document) {
}
