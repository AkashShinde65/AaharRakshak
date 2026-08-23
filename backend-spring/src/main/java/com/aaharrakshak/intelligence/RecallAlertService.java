package com.aaharrakshak.intelligence;

import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.complaint.ComplaintRepository;
import com.aaharrakshak.investigation.Action;
import com.aaharrakshak.investigation.ActionType;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecallAlertService {

    private final ComplaintRepository complaintRepository;
    private final AlertOutboxService alertOutboxService;

    public RecallAlertService(ComplaintRepository complaintRepository, AlertOutboxService alertOutboxService) {
        this.complaintRepository = complaintRepository;
        this.alertOutboxService = alertOutboxService;
    }

    @Transactional
    public void notifyAffectedUsers(Action action) {
        if (action.getType() != ActionType.BATCH_RECALL || action.getComplaint().getBatch() == null) {
            return;
        }
        String district = action.getComplaint().getDistrict();
        List<Complaint> affectedComplaints = complaintRepository.findAffectedBatchComplaints(
                action.getComplaint().getBatch().getId(),
                district);
        Set<Long> notifiedCitizenIds = new LinkedHashSet<>();
        affectedComplaints.stream()
                .filter(complaint -> notifiedCitizenIds.add(complaint.getCitizen().getId()))
                .forEach(complaint -> alertOutboxService.enqueue(
                        complaint.getCitizen(),
                        "LOCATION_BATCH_RECALL",
                        "Recall and safety alert",
                        "A simulated recall alert was published for batch "
                                + action.getComplaint().getBatch().getBatchNumber()
                                + " in " + district + ". This is a demo platform alert, not a real government action.",
                        Map.of(
                                "actionNumber", action.getActionNumber(),
                                "ticketNumber", action.getComplaint().getTicketNumber(),
                                "batchNumber", action.getComplaint().getBatch().getBatchNumber(),
                                "district", district == null ? "" : district),
                        action.getComplaint().getLocationText(),
                        action.getCompany(),
                        action.getComplaint().getProduct(),
                        action.getComplaint().getBatch(),
                        List.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL, NotificationChannel.PUSH, NotificationChannel.SMS)));
    }
}
