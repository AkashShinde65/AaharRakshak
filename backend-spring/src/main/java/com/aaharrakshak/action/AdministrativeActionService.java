package com.aaharrakshak.action;

import com.aaharrakshak.action.dto.AdminActionDashboardItemResponse;
import com.aaharrakshak.action.dto.AdministrativeActionHistoryResponse;
import com.aaharrakshak.action.dto.AdministrativeActionResponse;
import com.aaharrakshak.action.dto.CompanyNoticeDocumentResponse;
import com.aaharrakshak.action.dto.CompanyNoticeResponseResponse;
import com.aaharrakshak.action.dto.ShowCauseNoticeResponse;
import com.aaharrakshak.audit.AuditService;
import com.aaharrakshak.catalog.Batch;
import com.aaharrakshak.catalog.BatchStatus;
import com.aaharrakshak.company.Company;
import com.aaharrakshak.company.CompanyRepository;
import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.complaint.ComplaintRepository;
import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.complaint.ComplaintStatusHistory;
import com.aaharrakshak.complaint.ComplaintStatusHistoryRepository;
import com.aaharrakshak.investigation.Action;
import com.aaharrakshak.investigation.ActionRepository;
import com.aaharrakshak.investigation.ActionType;
import com.aaharrakshak.investigation.AssignmentRepository;
import com.aaharrakshak.investigation.ComplaintWorkflowValidator;
import com.aaharrakshak.investigation.LabOutcome;
import com.aaharrakshak.investigation.LabReport;
import com.aaharrakshak.investigation.LabReportRepository;
import com.aaharrakshak.investigation.LabReportStatus;
import com.aaharrakshak.intelligence.ExternalAccountEventPublisher;
import com.aaharrakshak.intelligence.RecallAlertService;
import com.aaharrakshak.notification.Notification;
import com.aaharrakshak.notification.NotificationRepository;
import com.aaharrakshak.notification.NotificationStatus;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.storage.FileMetadataRequest;
import com.aaharrakshak.storage.FileStorageService;
import com.aaharrakshak.storage.StoredFileMetadata;
import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.User;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdministrativeActionService {

    private static final DateTimeFormatter NUMBER_DATE = DateTimeFormatter.ofPattern("yyyyMMdd")
            .withZone(ZoneOffset.UTC);

    private final LabReportRepository labReportRepository;
    private final ShowCauseNoticeRepository noticeRepository;
    private final CompanyNoticeResponseRepository responseRepository;
    private final AdministrativeActionHistoryRepository historyRepository;
    private final ActionRepository actionRepository;
    private final CompanyRepository companyRepository;
    private final AssignmentRepository assignmentRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;
    private final ComplaintWorkflowValidator workflowValidator;
    private final FileStorageService fileStorageService;
    private final AdministrativeDocumentValidator documentValidator;
    private final SafetyAlertRepository safetyAlertRepository;
    private final NotificationRepository notificationRepository;
    private final AuditService auditService;
    private final RecallAlertService recallAlertService;
    private final ExternalAccountEventPublisher externalAccountEventPublisher;
    private final SecureRandom secureRandom = new SecureRandom();

    public AdministrativeActionService(
            LabReportRepository labReportRepository,
            ShowCauseNoticeRepository noticeRepository,
            CompanyNoticeResponseRepository responseRepository,
            AdministrativeActionHistoryRepository historyRepository,
            ActionRepository actionRepository,
            CompanyRepository companyRepository,
            AssignmentRepository assignmentRepository,
            ComplaintRepository complaintRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository,
            ComplaintWorkflowValidator workflowValidator,
            FileStorageService fileStorageService,
            AdministrativeDocumentValidator documentValidator,
            SafetyAlertRepository safetyAlertRepository,
            NotificationRepository notificationRepository,
            AuditService auditService,
            RecallAlertService recallAlertService,
            ExternalAccountEventPublisher externalAccountEventPublisher) {
        this.labReportRepository = labReportRepository;
        this.noticeRepository = noticeRepository;
        this.responseRepository = responseRepository;
        this.historyRepository = historyRepository;
        this.actionRepository = actionRepository;
        this.companyRepository = companyRepository;
        this.assignmentRepository = assignmentRepository;
        this.complaintRepository = complaintRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.workflowValidator = workflowValidator;
        this.fileStorageService = fileStorageService;
        this.documentValidator = documentValidator;
        this.safetyAlertRepository = safetyAlertRepository;
        this.notificationRepository = notificationRepository;
        this.auditService = auditService;
        this.recallAlertService = recallAlertService;
        this.externalAccountEventPublisher = externalAccountEventPublisher;
    }

    @Transactional(readOnly = true)
    public List<AdminActionDashboardItemResponse> dashboard(AuthenticatedUser principal) {
        requireSeniorOfficial(principal);
        return labReportRepository.findByStatusOrderByPublishedAtDesc(LabReportStatus.PUBLISHED).stream()
                .map(this::toDashboardItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowCauseNoticeResponse> notices(AuthenticatedUser principal) {
        requireSeniorOfficial(principal);
        return noticeRepository.findAllByOrderByIssuedAtDesc().stream()
                .map(this::toNoticeResponse)
                .toList();
    }

    @Transactional
    public ShowCauseNoticeResponse issueNotice(
            AuthenticatedUser principal,
            Long reportId,
            IssueShowCauseNoticeRequest request) {
        requireSeniorOfficial(principal);
        LabReport report = loadReport(reportId);
        if (report.getStatus() != LabReportStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only published lab reports can receive notices");
        }
        if (report.getOutcome() == LabOutcome.SAFE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Safe lab outcomes do not require show-cause notices");
        }
        if (noticeRepository.findFirstByLabReportIdOrderByIssuedAtDesc(reportId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A show-cause notice already exists for this report");
        }
        Complaint complaint = report.getSample().getComplaint();
        Company company = complaint.getCompany();
        if (company == null || company.getOwnerUser() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Complaint is not linked to a registered company");
        }
        ShowCauseNotice notice = noticeRepository.save(new ShowCauseNotice(
                generateNoticeNumber(),
                complaint,
                report,
                company,
                principal.getUser(),
                request.subject(),
                request.reason(),
                request.evidenceSummary(),
                request.responseDueAt()));
        historyRepository.save(new AdministrativeActionHistory(
                complaint,
                notice,
                null,
                principal.getUser(),
                "SHOW_CAUSE_NOTICE_ISSUED",
                request.reason()));
        auditService.record(principal.getUser(), "SHOW_CAUSE_NOTICE_ISSUED", "NOTICE", notice.getNoticeNumber(),
                "Simulated due-process notice for " + complaint.getTicketNumber());
        notifyCompany(company, "Show-cause notice issued",
                "Notice " + notice.getNoticeNumber() + " requires your response by " + notice.getResponseDueAt() + ".");
        return toNoticeResponse(notice);
    }

    @Transactional(readOnly = true)
    public List<ShowCauseNoticeResponse> companyNotices(AuthenticatedUser principal) {
        return noticeRepository.findByCompanyOwnerUserIdOrderByIssuedAtDesc(principal.getUserId()).stream()
                .map(this::toNoticeResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShowCauseNoticeResponse noticeDetails(AuthenticatedUser principal, String noticeNumber) {
        ShowCauseNotice notice = loadNotice(noticeNumber);
        if (!isSeniorOfficial(principal) && !notice.getCompany().getOwnerUser().getId().equals(principal.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Notice is not visible to this user");
        }
        return toNoticeResponse(notice);
    }

    @Transactional
    public ShowCauseNoticeResponse submitCompanyResponse(
            AuthenticatedUser principal,
            String noticeNumber,
            CompanyNoticeResponseRequest request) {
        ShowCauseNotice notice = loadNotice(noticeNumber);
        ensureCompanyOwnsNotice(principal, notice);
        if (notice.getStatus() == AdministrativeNoticeStatus.DECIDED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Notice has already received a final decision");
        }
        documentValidator.validate(request.document());
        StoredFileMetadata storedFile = fileStorageService.storeMetadata(
                "company-notice-responses",
                new FileMetadataRequest(
                        request.document().objectKey(),
                        request.document().originalFileName(),
                        request.document().contentType().toLowerCase(Locale.ROOT),
                        request.document().sizeBytes()));
        responseRepository.save(new CompanyNoticeResponse(
                notice,
                principal.getUser(),
                request.responseText(),
                storedFile,
                request.document().checksumSha256().toLowerCase(Locale.ROOT)));
        notice.markResponded();
        historyRepository.save(new AdministrativeActionHistory(
                notice.getComplaint(),
                notice,
                null,
                principal.getUser(),
                "COMPANY_RESPONSE_SUBMITTED",
                "Company response and document metadata received"));
        auditService.record(principal.getUser(), "COMPANY_NOTICE_RESPONSE_SUBMITTED", "NOTICE",
                notice.getNoticeNumber(), "Document checksum recorded");
        return toNoticeResponse(notice);
    }

    @Transactional
    public ShowCauseNoticeResponse reviewNotice(
            AuthenticatedUser principal,
            String noticeNumber,
            ReviewNoticeRequest request) {
        requireSeniorOfficial(principal);
        ShowCauseNotice notice = loadNotice(noticeNumber);
        if (notice.getStatus() != AdministrativeNoticeStatus.RESPONDED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company response must be submitted before review");
        }
        notice.markUnderReview();
        historyRepository.save(new AdministrativeActionHistory(
                notice.getComplaint(),
                notice,
                null,
                principal.getUser(),
                "SENIOR_OFFICIAL_REVIEWED_RESPONSE",
                request.notes()));
        auditService.record(principal.getUser(), "SHOW_CAUSE_RESPONSE_REVIEWED", "NOTICE",
                notice.getNoticeNumber(), request.notes());
        return toNoticeResponse(notice);
    }

    @Transactional
    public AdministrativeActionResponse decide(
            AuthenticatedUser principal,
            String noticeNumber,
            AdministrativeDecisionRequest request) {
        requireSeniorOfficial(principal);
        ShowCauseNotice notice = loadNotice(noticeNumber);
        if (notice.getStatus() != AdministrativeNoticeStatus.UNDER_REVIEW
                && notice.getStatus() != AdministrativeNoticeStatus.RESPONDED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Notice response must be reviewed before decision");
        }
        if (actionRepository.findFirstByLabReportIdOrderByDecidedAtDesc(notice.getLabReport().getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A final administrative decision already exists");
        }
        ensureNotOwnCase(principal, notice);

        Action action = actionRepository.save(new Action(
                notice.getComplaint(),
                notice.getLabReport(),
                notice.getCompany(),
                notice,
                generateActionNumber(),
                principal.getUser(),
                request.actionType(),
                request.reason(),
                request.evidenceSummary(),
                request.effectiveDate(),
                request.publicSummary()));
        applySimulatedOperationalStatus(action);
        notice.markDecided();
        transitionComplaint(notice.getComplaint(), ComplaintStatus.ACTION_TAKEN, principal.getUser(),
                "Simulated administrative decision recorded");
        historyRepository.save(new AdministrativeActionHistory(
                notice.getComplaint(),
                notice,
                action,
                principal.getUser(),
                "FINAL_ADMINISTRATIVE_DECISION_RECORDED",
                request.actionType().name()));
        auditService.record(principal.getUser(), "ADMINISTRATIVE_ACTION_APPROVED", "ACTION",
                action.getActionNumber(), "Simulated only; no real government action performed");
        recallAlertService.notifyAffectedUsers(action);
        externalAccountEventPublisher.publish(action);
        notifyCompany(notice.getCompany(), "Administrative decision recorded",
                "Decision " + action.getActionNumber() + " has been recorded as simulated platform action.");
        notificationRepository.save(new Notification(
                notice.getComplaint().getCitizen(),
                "IN_APP",
                "Complaint status update",
                "Ticket " + notice.getComplaint().getTicketNumber()
                        + ": An administrative decision has been recorded after official review.",
                NotificationStatus.SENT));
        return toActionResponse(action);
    }

    private void applySimulatedOperationalStatus(Action action) {
        if (action.getType() == ActionType.BATCH_RECALL && action.getComplaint().getBatch() != null) {
            Batch batch = action.getComplaint().getBatch();
            batch.update(batch.getBatchNumber(), batch.getManufacturedOn(), batch.getExpiresOn(), BatchStatus.RECALLED);
        }
        if (action.getType() != ActionType.WARNING) {
            safetyAlertRepository.save(new SafetyAlert(
                    action,
                    switch (action.getType()) {
                        case BATCH_RECALL -> "Batch recall notice";
                        case TEMPORARY_SUSPENSION -> "Simulated temporary suspension notice";
                        case CANCELLATION -> "Simulated cancellation notice";
                        case WARNING -> "Warning notice";
                    },
                    action.getPublicSummary(),
                    action.getType() == ActionType.BATCH_RECALL ? "HIGH" : "MEDIUM"));
        }
    }

    private void ensureNotOwnCase(AuthenticatedUser principal, ShowCauseNotice notice) {
        Long userId = principal.getUserId();
        if (notice.getLabReport().getSubmittedBy() != null
                && notice.getLabReport().getSubmittedBy().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lab officer cannot approve their own case");
        }
        if (assignmentRepository.existsByComplaintIdAndAssignedToId(notice.getComplaint().getId(), userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Inspector cannot approve their own case");
        }
    }

    private void transitionComplaint(Complaint complaint, ComplaintStatus next, User actor, String notes) {
        ComplaintStatus current = complaint.getStatus();
        workflowValidator.assertTransition(current, next);
        if (current == next) {
            return;
        }
        complaint.changeStatus(next);
        complaintRepository.save(complaint);
        statusHistoryRepository.save(new ComplaintStatusHistory(complaint, next, actor, notes));
    }

    private AdminActionDashboardItemResponse toDashboardItem(LabReport report) {
        ShowCauseNotice notice = noticeRepository.findFirstByLabReportIdOrderByIssuedAtDesc(report.getId()).orElse(null);
        Action action = actionRepository.findFirstByLabReportIdOrderByDecidedAtDesc(report.getId()).orElse(null);
        Complaint complaint = report.getSample().getComplaint();
        return new AdminActionDashboardItemResponse(
                report.getId(),
                report.getReportNumber(),
                complaint.getTicketNumber(),
                complaint.getStatus(),
                report.getOutcome(),
                companyName(complaint),
                productName(complaint),
                batchNumber(complaint),
                complaint.getDistrict(),
                notice == null ? null : notice.getNoticeNumber(),
                notice == null ? null : notice.getStatus(),
                action == null ? null : action.getType(),
                notice == null ? null : notice.getResponseDueAt(),
                report.getPublishedAt());
    }

    private ShowCauseNoticeResponse toNoticeResponse(ShowCauseNotice notice) {
        return new ShowCauseNoticeResponse(
                notice.getId(),
                notice.getNoticeNumber(),
                notice.getComplaint().getTicketNumber(),
                notice.getLabReport().getReportNumber(),
                notice.getLabReport().getOutcome(),
                notice.getCompany().getId(),
                notice.getCompany().getLegalName(),
                productName(notice.getComplaint()),
                batchNumber(notice.getComplaint()),
                notice.getSubject(),
                notice.getReason(),
                notice.getEvidenceSummary(),
                notice.getResponseDueAt(),
                notice.getStatus(),
                notice.getIssuedAt(),
                responseRepository.findByNoticeIdOrderBySubmittedAtAsc(notice.getId()).stream()
                        .map(this::toCompanyResponse)
                        .toList(),
                actionRepository.findFirstByLabReportIdOrderByDecidedAtDesc(notice.getLabReport().getId())
                        .map(this::toActionResponse)
                        .orElse(null),
                historyRepository.findByNoticeIdOrderByCreatedAtAsc(notice.getId()).stream()
                        .map(this::toHistoryResponse)
                        .toList());
    }

    private CompanyNoticeResponseResponse toCompanyResponse(CompanyNoticeResponse response) {
        return new CompanyNoticeResponseResponse(
                response.getId(),
                response.getResponseText(),
                new CompanyNoticeDocumentResponse(
                        response.getObjectKey(),
                        response.getOriginalFileName(),
                        response.getContentType(),
                        response.getFileSizeBytes(),
                        response.getChecksumSha256()),
                response.getSubmittedAt());
    }

    private AdministrativeActionResponse toActionResponse(Action action) {
        return new AdministrativeActionResponse(
                action.getId(),
                action.getActionNumber(),
                action.getComplaint().getTicketNumber(),
                action.getLabReport().getReportNumber(),
                action.getCompany().getLegalName(),
                action.getType(),
                action.getReason(),
                action.getEvidenceSummary(),
                action.getEffectiveDate(),
                action.getDecidedBy().getId(),
                action.getDecidedBy().getFullName(),
                action.getSimulated(),
                action.getPublicSummary(),
                action.getDecidedAt());
    }

    private AdministrativeActionHistoryResponse toHistoryResponse(AdministrativeActionHistory history) {
        return new AdministrativeActionHistoryResponse(
                history.getEventType(),
                history.getNotes(),
                history.getActor() == null ? null : history.getActor().getFullName(),
                history.getCreatedAt());
    }

    private LabReport loadReport(Long reportId) {
        return labReportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab report not found"));
    }

    private ShowCauseNotice loadNotice(String noticeNumber) {
        return noticeRepository.findByNoticeNumber(noticeNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"));
    }

    private void ensureCompanyOwnsNotice(AuthenticatedUser principal, ShowCauseNotice notice) {
        Company company = companyRepository.findByOwnerUserId(principal.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company profile not found"));
        if (!notice.getCompany().getId().equals(company.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Notice is not assigned to this company");
        }
    }

    private void requireSeniorOfficial(AuthenticatedUser principal) {
        if (!isSeniorOfficial(principal)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Senior official role required");
        }
    }

    private boolean isSeniorOfficial(AuthenticatedUser principal) {
        return principal.getRoles().contains(RoleName.DISTRICT_ESCALATION_OFFICER)
                || principal.getRoles().contains(RoleName.CENTRAL_ADMINISTRATOR);
    }

    private void notifyCompany(Company company, String subject, String body) {
        notificationRepository.save(new Notification(company.getOwnerUser(), "IN_APP", subject, body, NotificationStatus.SENT));
    }

    private String generateNoticeNumber() {
        return generateNumber("SCN-", noticeRepository::existsByNoticeNumber);
    }

    private String generateActionNumber() {
        return generateNumber("ADM-", actionRepository::existsByActionNumber);
    }

    private String generateNumber(String prefix, java.util.function.Predicate<String> exists) {
        String date = NUMBER_DATE.format(Instant.now());
        for (int attempt = 0; attempt < 10; attempt++) {
            String number = prefix + date + "-" + String.format("%06d", secureRandom.nextInt(1_000_000));
            if (!exists.test(number)) {
                return number;
            }
        }
        throw new IllegalStateException("Could not generate unique administrative number");
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
}
