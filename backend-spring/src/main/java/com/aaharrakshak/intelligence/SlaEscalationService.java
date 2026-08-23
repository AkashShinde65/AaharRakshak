package com.aaharrakshak.intelligence;

import com.aaharrakshak.audit.AuditService;
import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.complaint.ComplaintRepository;
import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.complaint.ComplaintStatusHistory;
import com.aaharrakshak.complaint.ComplaintStatusHistoryRepository;
import com.aaharrakshak.intelligence.dto.CloseComplaintRequest;
import com.aaharrakshak.intelligence.dto.SlaEscalationResponse;
import com.aaharrakshak.investigation.Assignment;
import com.aaharrakshak.investigation.AssignmentRepository;
import com.aaharrakshak.investigation.ComplaintWorkflowValidator;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.User;
import com.aaharrakshak.user.UserRepository;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SlaEscalationService {

    private static final Set<ComplaintStatus> SLA_STATUSES = EnumSet.of(
            ComplaintStatus.SUBMITTED,
            ComplaintStatus.VERIFIED,
            ComplaintStatus.ASSIGNED,
            ComplaintStatus.INSPECTION_SCHEDULED,
            ComplaintStatus.SAMPLE_COLLECTED,
            ComplaintStatus.LAB_TESTING);

    private final ComplaintRepository complaintRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;
    private final SlaEscalationRepository escalationRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final ComplaintWorkflowValidator workflowValidator;
    private final AuditService auditService;
    private final AlertOutboxService alertOutboxService;
    private final int highRiskThreshold;

    public SlaEscalationService(
            ComplaintRepository complaintRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository,
            SlaEscalationRepository escalationRepository,
            AssignmentRepository assignmentRepository,
            UserRepository userRepository,
            ComplaintWorkflowValidator workflowValidator,
            AuditService auditService,
            AlertOutboxService alertOutboxService,
            @Value("${app.intelligence.high-risk-threshold}") int highRiskThreshold) {
        this.complaintRepository = complaintRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.escalationRepository = escalationRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.workflowValidator = workflowValidator;
        this.auditService = auditService;
        this.alertOutboxService = alertOutboxService;
        this.highRiskThreshold = highRiskThreshold;
    }

    @Scheduled(
            initialDelayString = "${app.intelligence.sla-check-initial-delay-ms:60000}",
            fixedDelayString = "${app.intelligence.sla-check-delay-ms:300000}")
    @Transactional
    public void scheduledOverdueCheck() {
        escalateOverdueInternal(null);
    }

    @Transactional
    public List<SlaEscalationResponse> triggerOverdueCheck(AuthenticatedUser principal) {
        requireSeniorOfficial(principal);
        escalateOverdueInternal(principal.getUser());
        return escalations(principal);
    }

    @Transactional(readOnly = true)
    public List<SlaEscalationResponse> escalations(AuthenticatedUser principal) {
        requireOfficial(principal);
        return escalationRepository.findAllByOrderByEscalatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void closeComplaint(AuthenticatedUser principal, String ticketNumber, CloseComplaintRequest request) {
        Complaint complaint = complaintRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
        if (isAssignedInspector(principal, complaint)
                && complaint.getRiskScore() >= highRiskThreshold
                && complaint.getStatus() != ComplaintStatus.REPORT_PUBLISHED
                && complaint.getStatus() != ComplaintStatus.ACTION_TAKEN) {
            auditService.record(principal.getUser(), "HIGH_RISK_CLOSE_BLOCKED", "COMPLAINT", ticketNumber,
                    "Inspector cannot silently close a verified high-risk case");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Inspector cannot silently close a verified high-risk case");
        }
        if (!isSeniorOfficial(principal) && !isAssignedInspector(principal, complaint)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Official is not assigned to this complaint");
        }
        workflowValidator.assertTransition(complaint.getStatus(), ComplaintStatus.CLOSED);
        complaint.changeStatus(ComplaintStatus.CLOSED);
        statusHistoryRepository.save(new ComplaintStatusHistory(complaint, ComplaintStatus.CLOSED, principal.getUser(), request.reason()));
        auditService.record(principal.getUser(), "COMPLAINT_CLOSED", "COMPLAINT", ticketNumber, request.reason());
    }

    private void escalateOverdueInternal(User actor) {
        List<Complaint> overdue = complaintRepository
                .findByStatusInAndRiskScoreGreaterThanEqualAndSlaDueAtBeforeOrderBySlaDueAtAsc(
                        SLA_STATUSES,
                        highRiskThreshold,
                        Instant.now());
        overdue.stream()
                .filter(complaint -> !escalationRepository.existsByComplaintId(complaint.getId()))
                .forEach(complaint -> escalate(complaint, actor));
    }

    private void escalate(Complaint complaint, User actor) {
        Assignment assignment = assignmentRepository.findFirstByComplaintIdOrderByAssignedAtDesc(complaint.getId()).orElse(null);
        User districtOfficer = userRepository.findByRoleName(RoleName.DISTRICT_ESCALATION_OFFICER).stream()
                .findFirst()
                .orElseGet(() -> userRepository.findByRoleName(RoleName.CENTRAL_ADMINISTRATOR).stream()
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No senior official seed user found")));
        ComplaintStatus previous = complaint.getStatus();
        workflowValidator.assertTransition(previous, ComplaintStatus.ESCALATED);
        complaint.changeStatus(ComplaintStatus.ESCALATED);
        statusHistoryRepository.save(new ComplaintStatusHistory(
                complaint,
                ComplaintStatus.ESCALATED,
                actor == null ? districtOfficer : actor,
                "High-risk complaint crossed SLA due date"));
        escalationRepository.save(new SlaEscalation(
                complaint,
                assignment == null ? null : assignment.getAssignedTo(),
                districtOfficer,
                previous,
                "High-risk complaint exceeded SLA due at " + complaint.getSlaDueAt()));
        auditService.record(actor == null ? districtOfficer : actor, "SLA_ESCALATED", "COMPLAINT",
                complaint.getTicketNumber(), "Administrative lapse recorded for overdue high-risk case");
        notifyEscalation(complaint, assignment == null ? null : assignment.getAssignedTo(), districtOfficer);
    }

    private void notifyEscalation(Complaint complaint, User inspector, User districtOfficer) {
        String body = "Ticket " + complaint.getTicketNumber()
                + " crossed the high-risk SLA and was escalated to district review.";
        if (inspector != null) {
            alertOutboxService.enqueue(
                    inspector,
                    "SLA_ESCALATION",
                    "High-risk SLA escalation",
                    body,
                    Map.of("ticketNumber", complaint.getTicketNumber(), "riskScore", complaint.getRiskScore()),
                    complaint.getLocationText(),
                    complaint.getCompany(),
                    complaint.getProduct(),
                    complaint.getBatch(),
                    List.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL));
        }
        alertOutboxService.enqueue(
                districtOfficer,
                "SLA_ESCALATION",
                "High-risk SLA escalation",
                body,
                Map.of("ticketNumber", complaint.getTicketNumber(), "riskScore", complaint.getRiskScore()),
                complaint.getLocationText(),
                complaint.getCompany(),
                complaint.getProduct(),
                complaint.getBatch(),
                List.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL, NotificationChannel.SMS));
    }

    private SlaEscalationResponse toResponse(SlaEscalation escalation) {
        return new SlaEscalationResponse(
                escalation.getId(),
                escalation.getComplaint().getTicketNumber(),
                escalation.getComplaint().getDistrict(),
                escalation.getComplaint().getRiskScore(),
                escalation.getPreviousStatus(),
                escalation.getAssignedInspector() == null ? null : escalation.getAssignedInspector().getFullName(),
                escalation.getEscalatedTo().getFullName(),
                escalation.getReason(),
                escalation.getEscalatedAt());
    }

    private boolean isAssignedInspector(AuthenticatedUser principal, Complaint complaint) {
        return principal.getRoles().contains(RoleName.FOOD_INSPECTOR)
                && assignmentRepository.existsByComplaintIdAndAssignedToId(complaint.getId(), principal.getUserId());
    }

    private void requireOfficial(AuthenticatedUser principal) {
        if (principal.getRoles().contains(RoleName.FOOD_INSPECTOR)
                || isSeniorOfficial(principal)
                || principal.getRoles().contains(RoleName.LABORATORY_OFFICER)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Official role required");
    }

    private void requireSeniorOfficial(AuthenticatedUser principal) {
        if (isSeniorOfficial(principal)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Senior official role required");
    }

    private boolean isSeniorOfficial(AuthenticatedUser principal) {
        return principal.getRoles().contains(RoleName.DISTRICT_ESCALATION_OFFICER)
                || principal.getRoles().contains(RoleName.CENTRAL_ADMINISTRATOR);
    }
}
