package com.aaharrakshak.investigation;

import com.aaharrakshak.audit.AuditService;
import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.complaint.ComplaintRepository;
import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.complaint.ComplaintStatusHistory;
import com.aaharrakshak.complaint.ComplaintStatusHistoryRepository;
import com.aaharrakshak.investigation.dto.AssignInspectorRequest;
import com.aaharrakshak.investigation.dto.InspectionCheckInRequest;
import com.aaharrakshak.investigation.dto.InspectionEvidenceResponse;
import com.aaharrakshak.investigation.dto.InspectionScheduleRequest;
import com.aaharrakshak.investigation.dto.InspectionVisitRecordRequest;
import com.aaharrakshak.investigation.dto.InspectionVisitResponse;
import com.aaharrakshak.investigation.dto.InvestigationComplaintResponse;
import com.aaharrakshak.investigation.dto.InvestigationDashboardComplaintResponse;
import com.aaharrakshak.investigation.dto.InvestigationFileMetadataRequest;
import com.aaharrakshak.investigation.dto.LabAssignmentRequest;
import com.aaharrakshak.investigation.dto.LabAssignmentResponse;
import com.aaharrakshak.investigation.dto.LabReportResponse;
import com.aaharrakshak.investigation.dto.LabReportUploadRequest;
import com.aaharrakshak.investigation.dto.LabTestResultRequest;
import com.aaharrakshak.investigation.dto.LabTestResultResponse;
import com.aaharrakshak.investigation.dto.ReportReviewRequest;
import com.aaharrakshak.investigation.dto.SampleChainEventResponse;
import com.aaharrakshak.investigation.dto.SampleCollectionRequest;
import com.aaharrakshak.investigation.dto.SampleReceivedRequest;
import com.aaharrakshak.investigation.dto.SampleResponse;
import com.aaharrakshak.notification.Notification;
import com.aaharrakshak.notification.NotificationRepository;
import com.aaharrakshak.notification.NotificationStatus;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.storage.FileMetadataRequest;
import com.aaharrakshak.storage.FileStorageService;
import com.aaharrakshak.storage.StoredFileMetadata;
import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.User;
import com.aaharrakshak.user.UserRepository;
import com.aaharrakshak.user.UserRoleRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InvestigationService {

    private static final DateTimeFormatter SAMPLE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd")
            .withZone(ZoneOffset.UTC);
    private static final Set<ComplaintStatus> ACTIVE_INVESTIGATION_STATUSES = EnumSet.of(
            ComplaintStatus.SUBMITTED,
            ComplaintStatus.VERIFIED,
            ComplaintStatus.ASSIGNED,
            ComplaintStatus.INSPECTION_SCHEDULED,
            ComplaintStatus.SAMPLE_COLLECTED,
            ComplaintStatus.LAB_TESTING);

    private final ComplaintRepository complaintRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final InspectionVisitRepository inspectionVisitRepository;
    private final InspectionEvidenceRepository inspectionEvidenceRepository;
    private final SampleRepository sampleRepository;
    private final SampleChainOfCustodyEventRepository custodyEventRepository;
    private final SampleLabAssignmentRepository sampleLabAssignmentRepository;
    private final LabReportRepository labReportRepository;
    private final LabTestResultRepository labTestResultRepository;
    private final FileStorageService fileStorageService;
    private final InvestigationFileValidator fileValidator;
    private final ComplaintWorkflowValidator workflowValidator;
    private final NotificationRepository notificationRepository;
    private final AuditService auditService;
    private final SecureRandom secureRandom = new SecureRandom();

    public InvestigationService(
            ComplaintRepository complaintRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository,
            AssignmentRepository assignmentRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            InspectionVisitRepository inspectionVisitRepository,
            InspectionEvidenceRepository inspectionEvidenceRepository,
            SampleRepository sampleRepository,
            SampleChainOfCustodyEventRepository custodyEventRepository,
            SampleLabAssignmentRepository sampleLabAssignmentRepository,
            LabReportRepository labReportRepository,
            LabTestResultRepository labTestResultRepository,
            FileStorageService fileStorageService,
            InvestigationFileValidator fileValidator,
            ComplaintWorkflowValidator workflowValidator,
            NotificationRepository notificationRepository,
            AuditService auditService) {
        this.complaintRepository = complaintRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.inspectionVisitRepository = inspectionVisitRepository;
        this.inspectionEvidenceRepository = inspectionEvidenceRepository;
        this.sampleRepository = sampleRepository;
        this.custodyEventRepository = custodyEventRepository;
        this.sampleLabAssignmentRepository = sampleLabAssignmentRepository;
        this.labReportRepository = labReportRepository;
        this.labTestResultRepository = labTestResultRepository;
        this.fileStorageService = fileStorageService;
        this.fileValidator = fileValidator;
        this.workflowValidator = workflowValidator;
        this.notificationRepository = notificationRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<InvestigationDashboardComplaintResponse> dashboard(AuthenticatedUser principal) {
        if (isInspectorOnly(principal)) {
            return assignmentRepository.findByAssignedToIdOrderByAssignedAtDesc(principal.getUserId()).stream()
                    .map(Assignment::getComplaint)
                    .map(this::toDashboardResponse)
                    .toList();
        }
        return complaintRepository.findByStatusInOrderByRiskScoreDescCreatedAtAsc(ACTIVE_INVESTIGATION_STATUSES)
                .stream()
                .map(this::toDashboardResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InvestigationDashboardComplaintResponse> assignedToInspector(AuthenticatedUser principal) {
        requireRole(principal, RoleName.FOOD_INSPECTOR);
        return assignmentRepository.findByAssignedToIdOrderByAssignedAtDesc(principal.getUserId()).stream()
                .map(Assignment::getComplaint)
                .map(this::toDashboardResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InvestigationComplaintResponse complaint(AuthenticatedUser principal, String ticketNumber) {
        Complaint complaint = loadComplaint(ticketNumber);
        ensureCanViewComplaint(principal, complaint);
        return toComplaintResponse(complaint);
    }

    @Transactional
    public InvestigationComplaintResponse verifyComplaint(AuthenticatedUser principal, String ticketNumber) {
        requireSeniorOfficial(principal);
        Complaint complaint = loadComplaint(ticketNumber);
        transitionComplaint(complaint, ComplaintStatus.VERIFIED, principal.getUser(), "Complaint verified for investigation");
        auditService.record(principal.getUser(), "COMPLAINT_VERIFIED", "COMPLAINT", complaint.getTicketNumber(),
                "Complaint entered official investigation queue");
        notifyCitizen(complaint, "Your complaint has been verified for investigation.");
        return toComplaintResponse(complaint);
    }

    @Transactional
    public InvestigationComplaintResponse assignInspector(
            AuthenticatedUser principal,
            String ticketNumber,
            AssignInspectorRequest request) {
        requireSeniorOfficial(principal);
        Complaint complaint = loadComplaint(ticketNumber);
        User inspector = userRepository.findById(request.inspectorUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inspector not found"));
        ensureUserHasRole(inspector, RoleName.FOOD_INSPECTOR);
        if (complaint.getStatus() == ComplaintStatus.SUBMITTED) {
            transitionComplaint(complaint, ComplaintStatus.VERIFIED, principal.getUser(), "Complaint verified before assignment");
        }
        if (complaint.getStatus() == ComplaintStatus.VERIFIED) {
            transitionComplaint(complaint, ComplaintStatus.ASSIGNED, principal.getUser(), "Complaint assigned to food inspector");
        } else if (complaint.getStatus() != ComplaintStatus.ASSIGNED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Complaint is not ready for inspector assignment");
        }
        String district = blankToNull(request.district());
        complaint.applyInvestigationAssignment(
                district == null ? deriveDistrict(complaint) : district,
                Instant.now().plusSeconds((request.slaHours() == null ? 72 : request.slaHours()) * 3600L));
        assignmentRepository.save(new Assignment(complaint, inspector, principal.getUser(), blankToNull(request.notes())));
        auditService.record(principal.getUser(), "COMPLAINT_ASSIGNED_TO_INSPECTOR", "COMPLAINT",
                complaint.getTicketNumber(), "Assigned to user " + inspector.getId());
        notifyCitizen(complaint, "Your complaint has been assigned for inspection.");
        return toComplaintResponse(complaint);
    }

    @Transactional
    public InspectionVisitResponse scheduleInspection(
            AuthenticatedUser principal,
            String ticketNumber,
            InspectionScheduleRequest request) {
        Complaint complaint = loadComplaint(ticketNumber);
        ensureCanInvestigateComplaint(principal, complaint);
        if (complaint.getStatus() == ComplaintStatus.ASSIGNED) {
            transitionComplaint(complaint, ComplaintStatus.INSPECTION_SCHEDULED, principal.getUser(),
                    "Inspection scheduled");
        } else if (complaint.getStatus() != ComplaintStatus.INSPECTION_SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Complaint is not ready for inspection scheduling");
        }
        User inspector = assignedInspector(complaint)
                .orElse(principal.getUser());
        InspectionVisit visit = inspectionVisitRepository.save(new InspectionVisit(
                complaint,
                inspector,
                principal.getUser(),
                request.scheduledAt(),
                blankToNull(request.locationText()) == null ? complaint.getLocationText() : blankToNull(request.locationText())));
        auditService.record(principal.getUser(), "INSPECTION_SCHEDULED", "COMPLAINT", complaint.getTicketNumber(),
                request.scheduledAt().toString());
        notifyCitizen(complaint, "Inspection has been scheduled for your complaint.");
        return toInspectionVisitResponse(visit);
    }

    @Transactional
    public InspectionVisitResponse checkIn(AuthenticatedUser principal, Long inspectionId, InspectionCheckInRequest request) {
        InspectionVisit visit = loadVisit(inspectionId);
        ensureCanHandleVisit(principal, visit);
        if (visit.getStatus() == InspectionVisitStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inspection visit is already completed");
        }
        visit.checkIn(request.latitude(), request.longitude(), blankToNull(request.locationText()));
        auditService.record(principal.getUser(), "INSPECTION_CHECK_IN", "INSPECTION_VISIT",
                visit.getId().toString(), visit.getComplaint().getTicketNumber());
        return toInspectionVisitResponse(visit);
    }

    @Transactional
    public InspectionVisitResponse recordVisit(
            AuthenticatedUser principal,
            Long inspectionId,
            InspectionVisitRecordRequest request) {
        InspectionVisit visit = loadVisit(inspectionId);
        ensureCanHandleVisit(principal, visit);
        visit.complete(request.visitedAt(), blankToNull(request.notes()));
        if (request.evidence() != null) {
            request.evidence().forEach(file -> inspectionEvidenceRepository.save(createInspectionEvidence(visit, file)));
        }
        auditService.record(principal.getUser(), "INSPECTION_VISIT_RECORDED", "INSPECTION_VISIT",
                visit.getId().toString(), visit.getComplaint().getTicketNumber());
        return toInspectionVisitResponse(visit);
    }

    @Transactional
    public SampleResponse collectSample(
            AuthenticatedUser principal,
            Long inspectionId,
            SampleCollectionRequest request) {
        InspectionVisit visit = loadVisit(inspectionId);
        ensureCanHandleVisit(principal, visit);
        if (visit.getStatus() != InspectionVisitStatus.COMPLETED && visit.getStatus() != InspectionVisitStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Record inspection visit before collecting samples");
        }
        if (sampleRepository.existsBySealNumber(request.sealNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seal number already exists");
        }
        Complaint complaint = visit.getComplaint();
        String sampleNumber = generateSampleNumber();
        Sample sample = sampleRepository.save(new Sample(
                complaint,
                visit,
                principal.getUser(),
                sampleNumber,
                request.sealNumber(),
                request.quantity(),
                blankToNull(request.chainOfCustodyNotes()),
                request.collectedAt(),
                request.latitude(),
                request.longitude(),
                request.locationText(),
                request.storageDetails()));
        custodyEventRepository.save(new SampleChainOfCustodyEvent(
                sample,
                SampleChainEventType.COLLECTED,
                principal.getUser(),
                null,
                principal.getUser(),
                request.locationText(),
                blankToNull(request.chainOfCustodyNotes())));
        transitionComplaint(complaint, ComplaintStatus.SAMPLE_COLLECTED, principal.getUser(), "Sample collected");
        auditService.record(principal.getUser(), "SAMPLE_COLLECTED", "SAMPLE", sample.getSampleNumber(),
                complaint.getTicketNumber());
        notifyCitizen(complaint, "Sample collection has been recorded for your complaint.");
        return toSampleResponse(sample);
    }

    @Transactional
    public LabAssignmentResponse assignLab(AuthenticatedUser principal, Long sampleId, LabAssignmentRequest request) {
        requireSeniorOfficial(principal);
        Sample sample = loadSample(sampleId);
        User labOfficer = userRepository.findById(request.labOfficerUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lab officer not found"));
        ensureUserHasRole(labOfficer, RoleName.LABORATORY_OFFICER);
        if (sample.getComplaint().getStatus() != ComplaintStatus.SAMPLE_COLLECTED
                && sample.getComplaint().getStatus() != ComplaintStatus.LAB_TESTING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sample is not ready for laboratory assignment");
        }
        SampleLabAssignment assignment = sampleLabAssignmentRepository.save(new SampleLabAssignment(
                sample,
                labOfficer,
                principal.getUser(),
                blankToNull(request.notes())));
        custodyEventRepository.save(new SampleChainOfCustodyEvent(
                sample,
                SampleChainEventType.TRANSFERRED_TO_LAB,
                principal.getUser(),
                sample.getCollectedBy(),
                labOfficer,
                sample.getLocationText(),
                "Assigned to laboratory officer"));
        auditService.record(principal.getUser(), "SAMPLE_ASSIGNED_TO_LAB", "SAMPLE", sample.getSampleNumber(),
                "Assigned to user " + labOfficer.getId());
        return toLabAssignmentResponse(assignment);
    }

    @Transactional(readOnly = true)
    public List<LabAssignmentResponse> assignedLabSamples(AuthenticatedUser principal) {
        requireRole(principal, RoleName.LABORATORY_OFFICER);
        return sampleLabAssignmentRepository.findByAssignedToIdOrderByAssignedAtDesc(principal.getUserId()).stream()
                .map(this::toLabAssignmentResponse)
                .toList();
    }

    @Transactional
    public LabAssignmentResponse receiveSample(
            AuthenticatedUser principal,
            Long sampleId,
            SampleReceivedRequest request) {
        requireRole(principal, RoleName.LABORATORY_OFFICER);
        Sample sample = loadSample(sampleId);
        SampleLabAssignment assignment = latestLabAssignment(sample);
        ensureLabAssignmentBelongsTo(principal, assignment);
        if (assignment.getStatus() == SampleLabAssignmentStatus.ASSIGNED) {
            assignment.markReceived(principal.getUser());
            custodyEventRepository.save(new SampleChainOfCustodyEvent(
                    sample,
                    SampleChainEventType.RECEIVED_BY_LAB,
                    principal.getUser(),
                    sample.getCollectedBy(),
                    principal.getUser(),
                    blankToNull(request.locationText()),
                    blankToNull(request.storageCondition()) == null
                            ? blankToNull(request.notes())
                            : blankToNull(request.storageCondition())));
            if (sample.getComplaint().getStatus() == ComplaintStatus.SAMPLE_COLLECTED) {
                transitionComplaint(sample.getComplaint(), ComplaintStatus.LAB_TESTING, principal.getUser(),
                        "Sample received by laboratory");
                notifyCitizen(sample.getComplaint(), "Your complaint sample has reached laboratory testing.");
            }
            auditService.record(principal.getUser(), "SAMPLE_RECEIVED_BY_LAB", "SAMPLE", sample.getSampleNumber(),
                    sample.getComplaint().getTicketNumber());
        }
        return toLabAssignmentResponse(assignment);
    }

    @Transactional
    public LabReportResponse createLabReportDraft(
            AuthenticatedUser principal,
            Long sampleId,
            LabReportUploadRequest request) {
        requireRole(principal, RoleName.LABORATORY_OFFICER);
        Sample sample = loadSample(sampleId);
        ensureReceivedLabAssignment(principal, sample);
        if (labReportRepository.existsByReportNumber(request.reportNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Report number already exists");
        }
        fileValidator.validateLabReport(request);
        StoredFileMetadata storedFile = fileStorageService.storeMetadata(
                "lab-reports",
                new FileMetadataRequest(
                        request.objectKey(),
                        request.originalFileName(),
                        request.contentType().toLowerCase(Locale.ROOT),
                request.sizeBytes()));
        LabReport report = labReportRepository.save(new LabReport(
                sample,
                request.reportNumber(),
                storedFile,
                request.checksumSha256().toLowerCase(Locale.ROOT),
                blankToNull(request.resultSummary()),
                request.outcome()));
        request.results().forEach(result -> labTestResultRepository.save(toLabTestResult(report, result)));
        auditService.record(principal.getUser(), "LAB_REPORT_DRAFT_CREATED", "LAB_REPORT", report.getReportNumber(),
                sample.getSampleNumber());
        return toLabReportResponse(report);
    }

    @Transactional
    public LabReportResponse submitLabReport(AuthenticatedUser principal, Long reportId) {
        requireRole(principal, RoleName.LABORATORY_OFFICER);
        LabReport report = loadReport(reportId);
        ensureReceivedLabAssignment(principal, report.getSample());
        if (report.getStatus() != LabReportStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only draft reports can be submitted");
        }
        report.submit(principal.getUser());
        custodyEventRepository.save(new SampleChainOfCustodyEvent(
                report.getSample(),
                SampleChainEventType.REPORT_SUBMITTED,
                principal.getUser(),
                principal.getUser(),
                null,
                report.getSample().getLocationText(),
                "Laboratory report submitted for official review"));
        auditService.record(principal.getUser(), "LAB_REPORT_SUBMITTED", "LAB_REPORT", report.getReportNumber(),
                report.getSample().getSampleNumber());
        return toLabReportResponse(report);
    }

    @Transactional
    public LabReportResponse reviewLabReport(AuthenticatedUser principal, Long reportId, ReportReviewRequest request) {
        requireSeniorOfficial(principal);
        LabReport report = loadReport(reportId);
        if (report.getStatus() != LabReportStatus.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only submitted reports can be reviewed");
        }
        report.review(principal.getUser());
        custodyEventRepository.save(new SampleChainOfCustodyEvent(
                report.getSample(),
                SampleChainEventType.REPORT_REVIEWED,
                principal.getUser(),
                null,
                null,
                report.getSample().getLocationText(),
                blankToNull(request.notes())));
        auditService.record(principal.getUser(), "LAB_REPORT_REVIEWED", "LAB_REPORT", report.getReportNumber(),
                blankToNull(request.notes()));
        return toLabReportResponse(report);
    }

    @Transactional
    public LabReportResponse publishLabReport(AuthenticatedUser principal, Long reportId, ReportReviewRequest request) {
        requireSeniorOfficial(principal);
        LabReport report = loadReport(reportId);
        if (report.getStatus() != LabReportStatus.REVIEWED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only reviewed reports can be published");
        }
        report.publish();
        Complaint complaint = report.getSample().getComplaint();
        transitionComplaint(complaint, ComplaintStatus.REPORT_PUBLISHED, principal.getUser(),
                "Laboratory report published");
        custodyEventRepository.save(new SampleChainOfCustodyEvent(
                report.getSample(),
                SampleChainEventType.REPORT_PUBLISHED,
                principal.getUser(),
                null,
                null,
                report.getSample().getLocationText(),
                blankToNull(request.notes())));
        auditService.record(principal.getUser(), "LAB_REPORT_PUBLISHED", "LAB_REPORT", report.getReportNumber(),
                "No automatic licence ban imposed");
        notifyCitizen(complaint, "A reviewed laboratory report has been published for your complaint.");
        return toLabReportResponse(report);
    }

    private InspectionEvidence createInspectionEvidence(InspectionVisit visit, InvestigationFileMetadataRequest request) {
        fileValidator.validateInspectionEvidence(request);
        StoredFileMetadata storedFile = fileStorageService.storeMetadata(
                "inspection-evidence",
                new FileMetadataRequest(
                        request.objectKey(),
                        request.originalFileName(),
                        request.contentType().toLowerCase(Locale.ROOT),
                        request.sizeBytes()));
        return new InspectionEvidence(
                visit,
                request.type(),
                storedFile,
                request.checksumSha256().toLowerCase(Locale.ROOT),
                request.capturedAt());
    }

    private LabTestResult toLabTestResult(LabReport report, LabTestResultRequest request) {
        return new LabTestResult(
                report,
                request.parameterName(),
                blankToNull(request.testMethod()),
                blankToNull(request.permissibleLimit()),
                request.resultValue(),
                blankToNull(request.unit()),
                request.compliant(),
                blankToNull(request.remarks()));
    }

    private void transitionComplaint(Complaint complaint, ComplaintStatus next, User actor, String notes) {
        ComplaintStatus current = complaint.getStatus();
        workflowValidator.assertTransition(current, next);
        if (current == next) {
            return;
        }
        complaint.changeStatus(next);
        statusHistoryRepository.save(new ComplaintStatusHistory(complaint, next, actor, notes));
    }

    private Complaint loadComplaint(String ticketNumber) {
        return complaintRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
    }

    private InspectionVisit loadVisit(Long inspectionId) {
        return inspectionVisitRepository.findById(inspectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection visit not found"));
    }

    private Sample loadSample(Long sampleId) {
        return sampleRepository.findById(sampleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sample not found"));
    }

    private LabReport loadReport(Long reportId) {
        return labReportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab report not found"));
    }

    private SampleLabAssignment latestLabAssignment(Sample sample) {
        return sampleLabAssignmentRepository.findFirstBySampleIdOrderByAssignedAtDesc(sample.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Sample is not assigned to a laboratory"));
    }

    private void ensureReceivedLabAssignment(AuthenticatedUser principal, Sample sample) {
        SampleLabAssignment assignment = latestLabAssignment(sample);
        ensureLabAssignmentBelongsTo(principal, assignment);
        if (assignment.getStatus() != SampleLabAssignmentStatus.RECEIVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sample receipt must be confirmed before reporting");
        }
    }

    private void ensureCanViewComplaint(AuthenticatedUser principal, Complaint complaint) {
        if (isSeniorOfficial(principal)) {
            return;
        }
        if (principal.getRoles().contains(RoleName.FOOD_INSPECTOR)
                && assignmentRepository.existsByComplaintIdAndAssignedToId(complaint.getId(), principal.getUserId())) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Official is not assigned to this complaint");
    }

    private void ensureCanInvestigateComplaint(AuthenticatedUser principal, Complaint complaint) {
        ensureCanViewComplaint(principal, complaint);
        if (principal.getRoles().contains(RoleName.FOOD_INSPECTOR) || isSeniorOfficial(principal)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Official cannot perform inspection actions");
    }

    private void ensureCanHandleVisit(AuthenticatedUser principal, InspectionVisit visit) {
        if (isSeniorOfficial(principal)) {
            return;
        }
        if (principal.getRoles().contains(RoleName.FOOD_INSPECTOR)
                && visit.getInspector().getId().equals(principal.getUserId())
                && assignmentRepository.existsByComplaintIdAndAssignedToId(visit.getComplaint().getId(), principal.getUserId())) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Inspector is not assigned to this visit");
    }

    private void ensureLabAssignmentBelongsTo(AuthenticatedUser principal, SampleLabAssignment assignment) {
        if (!assignment.getAssignedTo().getId().equals(principal.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lab officer is not assigned to this sample");
        }
    }

    private void requireSeniorOfficial(AuthenticatedUser principal) {
        if (!isSeniorOfficial(principal)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Senior official role required");
        }
    }

    private void requireRole(AuthenticatedUser principal, RoleName roleName) {
        if (!principal.getRoles().contains(roleName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, roleName + " role required");
        }
    }

    private boolean isInspectorOnly(AuthenticatedUser principal) {
        return principal.getRoles().contains(RoleName.FOOD_INSPECTOR) && !isSeniorOfficial(principal);
    }

    private boolean isSeniorOfficial(AuthenticatedUser principal) {
        return principal.getRoles().contains(RoleName.DISTRICT_ESCALATION_OFFICER)
                || principal.getRoles().contains(RoleName.CENTRAL_ADMINISTRATOR);
    }

    private void ensureUserHasRole(User user, RoleName roleName) {
        boolean hasRole = userRoleRepository.findByUserId(user.getId()).stream()
                .anyMatch(userRole -> userRole.getRole().getName() == roleName);
        if (!hasRole) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have role " + roleName);
        }
    }

    private java.util.Optional<User> assignedInspector(Complaint complaint) {
        return assignmentRepository.findFirstByComplaintIdOrderByAssignedAtDesc(complaint.getId())
                .map(Assignment::getAssignedTo);
    }

    private void notifyCitizen(Complaint complaint, String body) {
        notificationRepository.save(new Notification(
                complaint.getCitizen(),
                "IN_APP",
                "Complaint status update",
                "Ticket " + complaint.getTicketNumber() + ": " + body,
                NotificationStatus.SENT));
    }

    private InvestigationDashboardComplaintResponse toDashboardResponse(Complaint complaint) {
        Assignment assignment = assignmentRepository.findFirstByComplaintIdOrderByAssignedAtDesc(complaint.getId())
                .orElse(null);
        User inspector = assignment == null ? null : assignment.getAssignedTo();
        return new InvestigationDashboardComplaintResponse(
                complaint.getId(),
                complaint.getTicketNumber(),
                complaint.getComplaintType(),
                complaint.getCategory(),
                complaint.getStatus(),
                complaint.getRiskScore(),
                complaint.getDistrict(),
                complaint.getSlaDueAt(),
                complaint.getSlaDueAt() != null && complaint.getSlaDueAt().isBefore(Instant.now()),
                complaint.getLatitude(),
                complaint.getLongitude(),
                complaint.getLocationText(),
                complaint.getVendorName(),
                complaint.getVendorAddress(),
                complaint.getProduct() == null ? complaint.getConfirmedProductName() : complaint.getProduct().getName(),
                complaint.getCompany() == null ? complaint.getConfirmedCompanyName() : complaint.getCompany().getLegalName(),
                inspector == null ? null : inspector.getId(),
                inspector == null ? null : inspector.getFullName());
    }

    private InvestigationComplaintResponse toComplaintResponse(Complaint complaint) {
        InvestigationDashboardComplaintResponse dashboard = toDashboardResponse(complaint);
        return new InvestigationComplaintResponse(
                dashboard.complaintId(),
                dashboard.ticketNumber(),
                dashboard.complaintType(),
                dashboard.category(),
                dashboard.status(),
                dashboard.riskScore(),
                dashboard.district(),
                dashboard.slaDueAt(),
                dashboard.overdue(),
                dashboard.latitude(),
                dashboard.longitude(),
                dashboard.address(),
                dashboard.vendorName(),
                dashboard.vendorAddress(),
                dashboard.productName(),
                dashboard.companyName(),
                dashboard.assignedInspectorId(),
                dashboard.assignedInspectorName(),
                inspectionVisitRepository.findByComplaintIdOrderByScheduledAtDesc(complaint.getId()).stream()
                        .map(this::toInspectionVisitResponse)
                        .toList(),
                sampleRepository.findByComplaintIdOrderByCollectedAtAsc(complaint.getId()).stream()
                        .map(this::toSampleResponse)
                        .toList());
    }

    private InspectionVisitResponse toInspectionVisitResponse(InspectionVisit visit) {
        return new InspectionVisitResponse(
                visit.getId(),
                visit.getComplaint().getTicketNumber(),
                visit.getInspector().getId(),
                visit.getInspector().getFullName(),
                visit.getScheduledAt(),
                visit.getStatus(),
                visit.getCheckInAt(),
                visit.getCheckInLatitude(),
                visit.getCheckInLongitude(),
                visit.getLocationText(),
                visit.getVisitNotes(),
                visit.getCompletedAt(),
                inspectionEvidenceRepository.findByInspectionVisitIdOrderByUploadedAtAsc(visit.getId()).stream()
                        .map(this::toInspectionEvidenceResponse)
                        .toList());
    }

    private InspectionEvidenceResponse toInspectionEvidenceResponse(InspectionEvidence evidence) {
        return new InspectionEvidenceResponse(
                evidence.getId(),
                evidence.getType(),
                evidence.getObjectKey(),
                evidence.getOriginalFileName(),
                evidence.getContentType(),
                evidence.getFileSizeBytes(),
                evidence.getChecksumSha256(),
                evidence.getCapturedAt(),
                evidence.getUploadedAt());
    }

    private SampleResponse toSampleResponse(Sample sample) {
        return new SampleResponse(
                sample.getId(),
                sample.getSampleNumber(),
                sample.getSealNumber(),
                sample.getComplaint().getTicketNumber(),
                sample.getQuantity(),
                sample.getCollectedAt(),
                sample.getLatitude(),
                sample.getLongitude(),
                sample.getLocationText(),
                sample.getStorageDetails(),
                custodyEventRepository.findBySampleIdOrderByEventAtAsc(sample.getId()).stream()
                        .map(this::toChainEventResponse)
                        .toList());
    }

    private SampleChainEventResponse toChainEventResponse(SampleChainOfCustodyEvent event) {
        return new SampleChainEventResponse(
                event.getEventType(),
                event.getLocationText(),
                event.getNotes(),
                event.getEventAt());
    }

    private LabAssignmentResponse toLabAssignmentResponse(SampleLabAssignment assignment) {
        Sample sample = assignment.getSample();
        return new LabAssignmentResponse(
                assignment.getId(),
                sample.getId(),
                sample.getSampleNumber(),
                sample.getComplaint().getTicketNumber(),
                assignment.getAssignedTo().getId(),
                assignment.getAssignedTo().getFullName(),
                assignment.getStatus(),
                assignment.getAssignedAt(),
                assignment.getReceivedAt(),
                assignment.getNotes());
    }

    private LabReportResponse toLabReportResponse(LabReport report) {
        Sample sample = report.getSample();
        return new LabReportResponse(
                report.getId(),
                sample.getId(),
                sample.getSampleNumber(),
                sample.getComplaint().getTicketNumber(),
                report.getReportNumber(),
                report.getStatus(),
                report.getOutcome(),
                report.getObjectKey(),
                report.getOriginalFileName(),
                report.getContentType(),
                report.getFileSizeBytes(),
                report.getChecksumSha256(),
                report.getResultSummary(),
                report.getUploadedAt(),
                report.getSubmittedAt(),
                report.getReviewedAt(),
                report.getPublishedAt(),
                labTestResultRepository.findByLabReportIdOrderByIdAsc(report.getId()).stream()
                        .map(this::toLabTestResultResponse)
                        .toList());
    }

    private LabTestResultResponse toLabTestResultResponse(LabTestResult result) {
        return new LabTestResultResponse(
                result.getId(),
                result.getParameterName(),
                result.getTestMethod(),
                result.getPermissibleLimit(),
                result.getResultValue(),
                result.getUnit(),
                result.getCompliant(),
                result.getRemarks());
    }

    private String generateSampleNumber() {
        String date = SAMPLE_DATE.format(Instant.now());
        for (int attempt = 0; attempt < 10; attempt++) {
            String sampleNumber = "SMP-" + date + "-" + randomDigits();
            if (!sampleRepository.existsBySampleNumber(sampleNumber)) {
                return sampleNumber;
            }
        }
        throw new IllegalStateException("Could not generate unique sample number");
    }

    private String randomDigits() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    private String deriveDistrict(Complaint complaint) {
        if (complaint.getDistrict() != null && !complaint.getDistrict().isBlank()) {
            return complaint.getDistrict();
        }
        String location = blankToNull(complaint.getLocationText());
        if (location == null) {
            return "Unspecified District";
        }
        int comma = location.indexOf(',');
        return comma > 0 ? location.substring(0, comma).trim() : location;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
