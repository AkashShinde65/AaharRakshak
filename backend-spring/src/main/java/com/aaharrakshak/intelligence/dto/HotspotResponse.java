package com.aaharrakshak.intelligence.dto;

import com.aaharrakshak.intelligence.RiskLevel;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record HotspotResponse(
        Long hotspotId,
        String hotspotKey,
        String district,
        String relatedKey,
        String productOrVendor,
        RiskLevel riskLevel,
        Integer complaintCount,
        BigDecimal radiusKm,
        BigDecimal centerLatitude,
        BigDecimal centerLongitude,
        Instant windowStart,
        Instant windowEnd,
        Instant detectedAt,
        List<String> complaintNumbers,
        String privacyNote) {
}
