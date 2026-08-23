package com.aaharrakshak.investigation.dto;

public record LabTestResultResponse(
        Long resultId,
        String parameterName,
        String testMethod,
        String permissibleLimit,
        String resultValue,
        String unit,
        Boolean compliant,
        String remarks) {
}
