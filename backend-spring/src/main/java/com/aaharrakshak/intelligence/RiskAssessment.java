package com.aaharrakshak.intelligence;

import java.util.List;

public record RiskAssessment(
        int score,
        RiskLevel riskLevel,
        List<String> reasons,
        String adapterName) {
}
