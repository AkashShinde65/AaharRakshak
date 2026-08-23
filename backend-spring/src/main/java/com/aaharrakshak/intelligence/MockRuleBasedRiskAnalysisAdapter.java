package com.aaharrakshak.intelligence;

import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.investigation.LabOutcome;
import com.aaharrakshak.investigation.LabReport;
import com.aaharrakshak.investigation.LabTestResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MockRuleBasedRiskAnalysisAdapter implements RiskAnalysisAdapter {

    @Override
    public RiskAssessment analyze(
            Complaint complaint,
            int relatedComplaintCount,
            List<LabReport> reports,
            List<LabTestResult> labResults) {
        int score = Math.max(complaint.getRiskScore(), 20);
        List<String> reasons = new ArrayList<>();
        reasons.add("Base complaint risk score: " + complaint.getRiskScore());
        if (relatedComplaintCount >= 10) {
            score += 25;
            reasons.add("Critical pattern: at least 10 related complaints in the configured area/time window");
        } else if (relatedComplaintCount >= 3) {
            score += 10;
            reasons.add("Related complaint pattern detected in the configured area/time window");
        }
        boolean nonCompliantParameter = labResults.stream().anyMatch(result -> !result.getCompliant());
        if (nonCompliantParameter) {
            score += 25;
            reasons.add("Published laboratory parameters include at least one non-compliant result");
        }
        boolean adulterated = reports.stream().anyMatch(report -> report.getOutcome() == LabOutcome.ADULTERATED);
        if (adulterated) {
            score += 30;
            reasons.add("Published laboratory outcome is ADULTERATED");
        }
        if (complaint.getComplaintType() != null) {
            reasons.add("Camera or OCR signals may suggest a category only; laboratory confirmation remains mandatory.");
        }
        int clamped = Math.max(0, Math.min(100, score));
        return new RiskAssessment(clamped, riskLevel(clamped), reasons, "mock-rule-based-risk-adapter");
    }

    private RiskLevel riskLevel(int score) {
        if (score >= 90) {
            return RiskLevel.CRITICAL;
        }
        if (score >= 70) {
            return RiskLevel.HIGH;
        }
        if (score >= 40) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.LOW;
    }
}
