package com.aaharrakshak.intelligence;

import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.investigation.LabReport;
import com.aaharrakshak.investigation.LabTestResult;
import java.util.List;

public interface RiskAnalysisAdapter {

    RiskAssessment analyze(Complaint complaint, int relatedComplaintCount, List<LabReport> reports, List<LabTestResult> labResults);
}
