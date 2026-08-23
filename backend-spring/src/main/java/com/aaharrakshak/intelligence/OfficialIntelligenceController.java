package com.aaharrakshak.intelligence;

import com.aaharrakshak.intelligence.dto.AlertOutboxResponse;
import com.aaharrakshak.intelligence.dto.CloseComplaintRequest;
import com.aaharrakshak.intelligence.dto.HotspotResponse;
import com.aaharrakshak.intelligence.dto.MockExternalEventResponse;
import com.aaharrakshak.intelligence.dto.RiskAnalysisResponse;
import com.aaharrakshak.intelligence.dto.SlaEscalationResponse;
import com.aaharrakshak.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/official/intelligence")
@Tag(name = "Advanced Intelligence")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('FOOD_INSPECTOR','LABORATORY_OFFICER','DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
public class OfficialIntelligenceController {

    private static final String EXTERNAL_SAFETY_NOTE =
            "Mock external events never disable or modify a real storefront, delivery or payment account.";

    private final HotspotService hotspotService;
    private final SlaEscalationService slaEscalationService;
    private final RiskAnalysisService riskAnalysisService;
    private final AlertOutboxService alertOutboxService;
    private final MockExternalEventRepository externalEventRepository;

    public OfficialIntelligenceController(
            HotspotService hotspotService,
            SlaEscalationService slaEscalationService,
            RiskAnalysisService riskAnalysisService,
            AlertOutboxService alertOutboxService,
            MockExternalEventRepository externalEventRepository) {
        this.hotspotService = hotspotService;
        this.slaEscalationService = slaEscalationService;
        this.riskAnalysisService = riskAnalysisService;
        this.alertOutboxService = alertOutboxService;
        this.externalEventRepository = externalEventRepository;
    }

    @GetMapping("/hotspots/district")
    @Operation(summary = "District-wise official hotspot dashboard with map-ready cluster centers")
    List<HotspotResponse> districtHotspots(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) String district) {
        return hotspotService.detectAndList(principal, district);
    }

    @PostMapping("/hotspots/detect")
    @Operation(summary = "Run configurable hotspot detection")
    List<HotspotResponse> detectHotspots(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) String district) {
        return hotspotService.detectAndList(principal, district);
    }

    @GetMapping("/sla/escalations")
    @Operation(summary = "Official SLA escalation dashboard")
    List<SlaEscalationResponse> escalations(@AuthenticationPrincipal AuthenticatedUser principal) {
        return slaEscalationService.escalations(principal);
    }

    @PostMapping("/sla/check-overdue")
    @PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
    @Operation(summary = "Run overdue high-risk SLA escalation check")
    List<SlaEscalationResponse> checkOverdue(@AuthenticationPrincipal AuthenticatedUser principal) {
        return slaEscalationService.triggerOverdueCheck(principal);
    }

    @PostMapping("/complaints/{ticketNumber}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Close a complaint only when workflow and high-risk safeguards allow it")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                      "reason": "Case closure requested after verified workflow outcome."
                    }
                    """)))
    void closeComplaint(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String ticketNumber,
            @Valid @RequestBody CloseComplaintRequest request) {
        slaEscalationService.closeComplaint(principal, ticketNumber, request);
    }

    @PostMapping("/risk/complaints/{ticketNumber}")
    @Operation(summary = "Run explainable mock AI risk analysis for a complaint")
    RiskAnalysisResponse analyzeRisk(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String ticketNumber) {
        return riskAnalysisService.analyze(principal, ticketNumber);
    }

    @GetMapping("/alerts/outbox")
    @PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
    @Operation(summary = "Durable alert outbox with retry status")
    List<AlertOutboxResponse> alertOutbox(@AuthenticationPrincipal AuthenticatedUser principal) {
        return alertOutboxService.allAlerts(principal);
    }

    @PostMapping("/alerts/retry")
    @PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
    @Operation(summary = "Retry due alert outbox records")
    int retryAlerts() {
        return alertOutboxService.retryDueAlerts();
    }

    @GetMapping("/mock-external-events")
    @PreAuthorize("hasAnyRole('DISTRICT_ESCALATION_OFFICER','CENTRAL_ADMINISTRATOR')")
    @Operation(summary = "List mock storefront, delivery and payment event publications")
    List<MockExternalEventResponse> mockExternalEvents() {
        return externalEventRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(event -> new MockExternalEventResponse(
                        event.getId(),
                        event.getEventType(),
                        event.getTargetType(),
                        event.getTargetId(),
                        event.getStatus(),
                        event.getCreatedAt(),
                        EXTERNAL_SAFETY_NOTE))
                .toList();
    }
}
