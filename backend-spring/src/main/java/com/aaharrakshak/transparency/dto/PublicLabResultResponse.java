package com.aaharrakshak.transparency.dto;

public record PublicLabResultResponse(
        String parameterName,
        String permissibleLimit,
        String resultValue,
        String unit,
        Boolean compliant,
        String remarks) {
}
