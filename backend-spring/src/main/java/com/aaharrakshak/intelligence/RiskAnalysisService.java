package com.aaharrakshak.intelligence;

import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.complaint.ComplaintRepository;
import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.complaint.FoodScanService;
import com.aaharrakshak.intelligence.dto.RiskAnalysisResponse;
import com.aaharrakshak.investigation.LabReport;
import com.aaharrakshak.investigation.LabReportRepository;
import com.aaharrakshak.investigation.LabReportStatus;
import com.aaharrakshak.investigation.LabTestResult;
import com.aaharrakshak.investigation.LabTestResultRepository;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.user.RoleName;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RiskAnalysisService {

    private static final Set<ComplaintStatus> PATTERN_STATUSES = EnumSet.of(
            ComplaintStatus.SUBMITTED,
            ComplaintStatus.VERIFIED,
            ComplaintStatus.ASSIGNED,
            ComplaintStatus.INSPECTION_SCHEDULED,
            ComplaintStatus.SAMPLE_COLLECTED,
            ComplaintStatus.LAB_TESTING,
            ComplaintStatus.REPORT_PUBLISHED,
            ComplaintStatus.ACTION_TAKEN,
            ComplaintStatus.ESCALATED);

    private final ComplaintRepository complaintRepository;
    private final LabReportRepository labReportRepository;
    private final LabTestResultRepository labTestResultRepository;
    private final RiskAnalysisRepository riskAnalysisRepository;
    private final RiskAnalysisAdapter riskAnalysisAdapter;
    private final GeoDistanceCalculator distanceCalculator;
    private final double radiusKm;
    private final long windowHours;

    public RiskAnalysisService(
            ComplaintRepository complaintRepository,
            LabReportRepository labReportRepository,
            LabTestResultRepository labTestResultRepository,
            RiskAnalysisRepository riskAnalysisRepository,
            RiskAnalysisAdapter riskAnalysisAdapter,
            GeoDistanceCalculator distanceCalculator,
            @Value("${app.intelligence.hotspot-radius-km}") double radiusKm,
            @Value("${app.intelligence.hotspot-window-hours}") long windowHours) {
        this.complaintRepository = complaintRepository;
        this.labReportRepository = labReportRepository;
        this.labTestResultRepository = labTestResultRepository;
        this.riskAnalysisRepository = riskAnalysisRepository;
        this.riskAnalysisAdapter = riskAnalysisAdapter;
        this.distanceCalculator = distanceCalculator;
        this.radiusKm = radiusKm;
        this.windowHours = windowHours;
    }

    @Transactional
    public RiskAnalysisResponse analyze(AuthenticatedUser principal, String ticketNumber) {
        requireOfficial(principal);
        Complaint complaint = complaintRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
        List<Complaint> related = relatedComplaints(complaint);
        List<LabReport> reports = labReportRepository.findFirstBySampleComplaintIdAndStatusOrderByPublishedAtDesc(
                        complaint.getId(),
                        LabReportStatus.PUBLISHED)
                .stream()
                .toList();
        List<LabTestResult> results = reports.stream()
                .flatMap(report -> labTestResultRepository.findByLabReportIdOrderByIdAsc(report.getId()).stream())
                .toList();
        RiskAssessment assessment = riskAnalysisAdapter.analyze(complaint, related.size(), reports, results);
        RiskAnalysis saved = riskAnalysisRepository.save(new RiskAnalysis(
                complaint,
                assessment.score(),
                assessment.riskLevel(),
                String.join(" | ", assessment.reasons()),
                assessment.adapterName(),
                FoodScanService.IMAGE_SAFETY_NOTE));
        return toResponse(saved);
    }

    private List<Complaint> relatedComplaints(Complaint complaint) {
        if (complaint.getLatitude() == null || complaint.getLongitude() == null) {
            return List.of();
        }
        Instant windowStart = Instant.now().minusSeconds(windowHours * 3600L);
        return complaintRepository
                .findByStatusInAndGpsConsentTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndSubmittedAtAfter(
                        PATTERN_STATUSES,
                        windowStart)
                .stream()
                .filter(candidate -> sameRelatedKey(complaint, candidate))
                .filter(candidate -> distanceCalculator.distanceKm(
                        complaint.getLatitude(),
                        complaint.getLongitude(),
                        candidate.getLatitude(),
                        candidate.getLongitude()) <= radiusKm)
                .toList();
    }

    private boolean sameRelatedKey(Complaint left, Complaint right) {
        if (left.getBatch() != null && right.getBatch() != null) {
            return left.getBatch().getId().equals(right.getBatch().getId());
        }
        if (left.getProduct() != null && right.getProduct() != null) {
            return left.getProduct().getId().equals(right.getProduct().getId());
        }
        return left.getVendorName() != null && left.getVendorName().equalsIgnoreCase(right.getVendorName());
    }

    private RiskAnalysisResponse toResponse(RiskAnalysis analysis) {
        return new RiskAnalysisResponse(
                analysis.getId(),
                analysis.getComplaint().getTicketNumber(),
                analysis.getScore(),
                analysis.getRiskLevel(),
                List.of(analysis.getReasons().split(" \\| ")),
                analysis.getAdapterName(),
                analysis.getImageSafetyNote(),
                analysis.getCreatedAt());
    }

    private void requireOfficial(AuthenticatedUser principal) {
        if (principal.getRoles().contains(RoleName.FOOD_INSPECTOR)
                || principal.getRoles().contains(RoleName.LABORATORY_OFFICER)
                || principal.getRoles().contains(RoleName.DISTRICT_ESCALATION_OFFICER)
                || principal.getRoles().contains(RoleName.CENTRAL_ADMINISTRATOR)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Official role required");
    }
}
