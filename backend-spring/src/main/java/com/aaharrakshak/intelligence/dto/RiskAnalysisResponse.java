package com.aaharrakshak.intelligence.dto;

import com.aaharrakshak.intelligence.RiskLevel;
import java.time.Instant;
import java.util.List;

public record RiskAnalysisResponse(
        Long analysisId,
        String ticketNumber,
        Integer score,
        RiskLevel riskLevel,
        List<String> reasons,
        String adapterName,
        String imageSafetyNote,
        Instant createdAt) {
}
