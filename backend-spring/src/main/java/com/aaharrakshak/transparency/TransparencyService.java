package com.aaharrakshak.transparency;

import com.aaharrakshak.action.SafetyAlert;
import com.aaharrakshak.action.SafetyAlertRepository;
import com.aaharrakshak.catalog.Batch;
import com.aaharrakshak.catalog.BatchRepository;
import com.aaharrakshak.company.Licence;
import com.aaharrakshak.company.LicenceRepository;
import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.complaint.ComplaintRepository;
import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.investigation.Action;
import com.aaharrakshak.investigation.ActionRepository;
import com.aaharrakshak.investigation.ActionType;
import com.aaharrakshak.investigation.LabReport;
import com.aaharrakshak.investigation.LabReportRepository;
import com.aaharrakshak.investigation.LabReportStatus;
import com.aaharrakshak.investigation.LabTestResult;
import com.aaharrakshak.investigation.LabTestResultRepository;
import com.aaharrakshak.transparency.dto.PublicAdministrativeActionResponse;
import com.aaharrakshak.transparency.dto.PublicBatchStatusResponse;
import com.aaharrakshak.transparency.dto.PublicComplaintStatusResponse;
import com.aaharrakshak.transparency.dto.PublicLabReportResponse;
import com.aaharrakshak.transparency.dto.PublicLabResultResponse;
import com.aaharrakshak.transparency.dto.PublicLicenceStatusResponse;
import com.aaharrakshak.transparency.dto.PublicSearchResultResponse;
import com.aaharrakshak.transparency.dto.SafetyAlertResponse;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransparencyService {

    private static final String PRIVACY_NOTICE = "This public report is anonymized. Citizen identity, phone, email, "
            + "exact private contact details, chain-of-custody internals and sensitive official notes are not exposed.";
    private static final Set<ComplaintStatus> PUBLIC_STATUSES = EnumSet.of(
            ComplaintStatus.REPORT_PUBLISHED,
            ComplaintStatus.ACTION_TAKEN,
            ComplaintStatus.CLOSED,
            ComplaintStatus.NO_VIOLATION_FOUND);

    private final ComplaintRepository complaintRepository;
    private final LabReportRepository labReportRepository;
    private final LabTestResultRepository labTestResultRepository;
    private final ActionRepository actionRepository;
    private final LicenceRepository licenceRepository;
    private final BatchRepository batchRepository;
    private final SafetyAlertRepository safetyAlertRepository;

    public TransparencyService(
            ComplaintRepository complaintRepository,
            LabReportRepository labReportRepository,
            LabTestResultRepository labTestResultRepository,
            ActionRepository actionRepository,
            LicenceRepository licenceRepository,
            BatchRepository batchRepository,
            SafetyAlertRepository safetyAlertRepository) {
        this.complaintRepository = complaintRepository;
        this.labReportRepository = labReportRepository;
        this.labTestResultRepository = labTestResultRepository;
        this.actionRepository = actionRepository;
        this.licenceRepository = licenceRepository;
        this.batchRepository = batchRepository;
        this.safetyAlertRepository = safetyAlertRepository;
    }

    @Transactional(readOnly = true)
    public PublicComplaintStatusResponse complaintStatus(String ticketNumber) {
        Complaint complaint = complaintRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
        ensurePublicComplaint(complaint);
        List<PublicLabReportResponse> reports = labReportRepository
                .findFirstBySampleComplaintIdAndStatusOrderByPublishedAtDesc(complaint.getId(), LabReportStatus.PUBLISHED)
                .map(report -> List.of(toPublicReport(report)))
                .orElse(List.of());
        return new PublicComplaintStatusResponse(
                complaint.getTicketNumber(),
                complaint.getComplaintType(),
                complaint.getStatus(),
                complaint.getCategory().name(),
                companyName(complaint),
                productName(complaint),
                batchNumber(complaint),
                complaint.getDistrict(),
                complaint.getSubmittedAt(),
                complaint.getUpdatedAt(),
                reports);
    }

    @Transactional(readOnly = true)
    public PublicLabReportResponse report(String reportNumber) {
        LabReport report = labReportRepository.findFirstByReportNumberOrderByUploadedAtDesc(reportNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        if (report.getStatus() != LabReportStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report is not publicly available");
        }
        ensurePublicComplaint(report.getSample().getComplaint());
        return toPublicReport(report);
    }

    @Transactional(readOnly = true)
    public List<PublicSearchResultResponse> search(
            String complaintNumber,
            String company,
            String product,
            String batch,
            String location) {
        return complaintRepository.searchPublic(
                        PUBLIC_STATUSES,
                        blankToNull(complaintNumber),
                        blankToNull(company),
                        blankToNull(product),
                        blankToNull(batch),
                        blankToNull(location))
                .stream()
                .map(this::toSearchResult)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicLicenceStatusResponse licenceStatus(String licenceNumber) {
        Licence licence = licenceRepository.findByLicenceNumber(licenceNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Licence not found"));
        String simulatedStatus = actionRepository
                .findByTypeInOrderByDecidedAtDesc(List.of(ActionType.TEMPORARY_SUSPENSION, ActionType.CANCELLATION))
                .stream()
                .filter(action -> action.getCompany() != null
                        && action.getCompany().getId().equals(licence.getCompany().getId()))
                .map(action -> action.getType().name() + " (SIMULATED)")
                .findFirst()
                .orElse("NONE");
        return new PublicLicenceStatusResponse(
                licence.getLicenceNumber(),
                licence.getCompany().getLegalName(),
                licence.getCompany().getStatus(),
                licence.getStatus(),
                simulatedStatus,
                licence.getValidTo(),
                "Simulated administrative statuses are demo records and do not perform real government action.");
    }

    @Transactional(readOnly = true)
    public PublicBatchStatusResponse batchStatus(String batchNumber) {
        Batch batch = batchRepository.findFirstByBatchNumberIgnoreCaseOrderByIdAsc(batchNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));
        return new PublicBatchStatusResponse(
                batch.getBatchNumber(),
                batch.getProduct().getName(),
                batch.getProduct().getCompany().getLegalName(),
                batch.getStatus(),
                batch.getManufacturedOn(),
                batch.getExpiresOn(),
                "Batch status is platform-demo status based on verified records, not raw allegations.");
    }

    @Transactional(readOnly = true)
    public List<PublicAdministrativeActionResponse> recallNotices() {
        return actionRepository.findByTypeInOrderByDecidedAtDesc(List.of(ActionType.BATCH_RECALL)).stream()
                .map(this::toPublicAction)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SafetyAlertResponse> safetyAlerts() {
        return safetyAlertRepository.findByActiveTrueOrderByPublishedAtDesc().stream()
                .map(this::toAlertResponse)
                .toList();
    }

    private PublicLabReportResponse toPublicReport(LabReport report) {
        Complaint complaint = report.getSample().getComplaint();
        return new PublicLabReportResponse(
                report.getReportNumber(),
                complaint.getTicketNumber(),
                report.getOutcome(),
                report.getResultSummary(),
                report.getPublishedAt(),
                companyName(complaint),
                productName(complaint),
                batchNumber(complaint),
                complaint.getDistrict(),
                labTestResultRepository.findByLabReportIdOrderByIdAsc(report.getId()).stream()
                        .map(this::toPublicResult)
                        .toList(),
                actionRepository.findFirstByLabReportIdOrderByDecidedAtDesc(report.getId())
                        .map(this::toPublicAction)
                        .orElse(null),
                PRIVACY_NOTICE);
    }

    private PublicLabResultResponse toPublicResult(LabTestResult result) {
        return new PublicLabResultResponse(
                result.getParameterName(),
                result.getPermissibleLimit(),
                result.getResultValue(),
                result.getUnit(),
                result.getCompliant(),
                result.getRemarks());
    }

    private PublicAdministrativeActionResponse toPublicAction(Action action) {
        return new PublicAdministrativeActionResponse(
                action.getActionNumber(),
                action.getType(),
                action.getEffectiveDate(),
                action.getSimulated(),
                action.getPublicSummary(),
                action.getDecidedAt());
    }

    private PublicSearchResultResponse toSearchResult(Complaint complaint) {
        LabReport report = labReportRepository
                .findFirstBySampleComplaintIdAndStatusOrderByPublishedAtDesc(complaint.getId(), LabReportStatus.PUBLISHED)
                .orElse(null);
        return new PublicSearchResultResponse(
                complaint.getTicketNumber(),
                complaint.getStatus(),
                companyName(complaint),
                productName(complaint),
                batchNumber(complaint),
                complaint.getDistrict(),
                report == null ? null : report.getOutcome(),
                report == null ? null : report.getPublishedAt());
    }

    private SafetyAlertResponse toAlertResponse(SafetyAlert alert) {
        return new SafetyAlertResponse(
                alert.getId(),
                alert.getTitle(),
                alert.getMessage(),
                alert.getSeverity(),
                alert.getCompany() == null ? null : alert.getCompany().getLegalName(),
                alert.getProduct() == null ? null : alert.getProduct().getName(),
                alert.getBatch() == null ? null : alert.getBatch().getBatchNumber(),
                alert.getLocationText(),
                alert.getPublishedAt());
    }

    private void ensurePublicComplaint(Complaint complaint) {
        if (!PUBLIC_STATUSES.contains(complaint.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint is not publicly available");
        }
    }

    private String companyName(Complaint complaint) {
        return complaint.getCompany() == null ? complaint.getConfirmedCompanyName() : complaint.getCompany().getLegalName();
    }

    private String productName(Complaint complaint) {
        return complaint.getProduct() == null ? complaint.getConfirmedProductName() : complaint.getProduct().getName();
    }

    private String batchNumber(Complaint complaint) {
        return complaint.getBatch() == null ? complaint.getConfirmedBatchNumber() : complaint.getBatch().getBatchNumber();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
