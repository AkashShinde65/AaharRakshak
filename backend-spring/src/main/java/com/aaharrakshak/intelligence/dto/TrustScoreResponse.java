package com.aaharrakshak.intelligence.dto;

import com.aaharrakshak.intelligence.RiskLevel;
import java.math.BigDecimal;
import java.time.Instant;

public record TrustScoreResponse(
        Long companyId,
        String companyName,
        BigDecimal score,
        RiskLevel riskLevel,
        BigDecimal inspectionPoints,
        BigDecimal labPoints,
        BigDecimal recallPoints,
        BigDecimal reviewPoints,
        Integer reviewCount,
        String explanation,
        String rawComplaintFairnessNote,
        Instant recalculatedAt) {
}
