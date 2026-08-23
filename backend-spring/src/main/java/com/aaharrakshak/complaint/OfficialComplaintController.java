package com.aaharrakshak.complaint;

import com.aaharrakshak.complaint.dto.ComplaintResponse;
import com.aaharrakshak.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/official/complaints")
@Tag(name = "Official Complaint Review")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('FOOD_INSPECTOR','LABORATORY_OFFICER','DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
public class OfficialComplaintController {

    private final ComplaintService complaintService;

    public OfficialComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/assigned")
    @Operation(summary = "List complaints assigned to the authenticated official")
    List<ComplaintResponse> assignedComplaints(@AuthenticationPrincipal AuthenticatedUser principal) {
        return complaintService.assignedComplaints(principal);
    }

    @GetMapping("/{ticketNumber}")
    @Operation(summary = "View assigned complaint details without citizen private contact details")
    ComplaintResponse complaint(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String ticketNumber) {
        return complaintService.officialComplaint(principal, ticketNumber);
    }
}
