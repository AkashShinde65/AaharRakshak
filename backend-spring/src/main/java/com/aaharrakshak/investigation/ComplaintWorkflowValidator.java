package com.aaharrakshak.investigation;

import com.aaharrakshak.complaint.ComplaintStatus;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ComplaintWorkflowValidator {

    private static final Map<ComplaintStatus, Set<ComplaintStatus>> ALLOWED_TRANSITIONS = Map.ofEntries(
            Map.entry(ComplaintStatus.DRAFT, Set.of(ComplaintStatus.SUBMITTED)),
            Map.entry(ComplaintStatus.SUBMITTED, Set.of(
                    ComplaintStatus.VERIFIED,
                    ComplaintStatus.INSUFFICIENT_EVIDENCE,
                    ComplaintStatus.REJECTED_DUPLICATE,
                    ComplaintStatus.ESCALATED)),
            Map.entry(ComplaintStatus.VERIFIED, Set.of(ComplaintStatus.ASSIGNED, ComplaintStatus.ESCALATED)),
            Map.entry(ComplaintStatus.ASSIGNED, Set.of(ComplaintStatus.INSPECTION_SCHEDULED, ComplaintStatus.ESCALATED)),
            Map.entry(ComplaintStatus.INSPECTION_SCHEDULED, Set.of(ComplaintStatus.SAMPLE_COLLECTED, ComplaintStatus.ESCALATED)),
            Map.entry(ComplaintStatus.SAMPLE_COLLECTED, Set.of(ComplaintStatus.LAB_TESTING, ComplaintStatus.ESCALATED)),
            Map.entry(ComplaintStatus.LAB_TESTING, Set.of(
                    ComplaintStatus.REPORT_PUBLISHED,
                    ComplaintStatus.NO_VIOLATION_FOUND,
                    ComplaintStatus.ESCALATED)),
            Map.entry(ComplaintStatus.REPORT_PUBLISHED, Set.of(ComplaintStatus.ACTION_TAKEN, ComplaintStatus.CLOSED)),
            Map.entry(ComplaintStatus.ACTION_TAKEN, Set.of(ComplaintStatus.CLOSED)));

    public void assertTransition(ComplaintStatus current, ComplaintStatus next) {
        if (current == next) {
            return;
        }
        Set<ComplaintStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(next)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Invalid complaint status transition from " + current + " to " + next);
        }
    }
}
